package com.mmkarton.mx7.reportgenerator.wizards;

/*
 ********************************************************************************
 * Copyright (c) 2009 Ing. Gerd Stockner (Mayr-Melnhof Karton Gesellschaft m.b.H.), Christian Voller (Mayr-Melnhof Karton Gesellschaft m.b.H.), CoSMIT GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Ing. Gerd Stockner (Mayr-Melnhof Karton Gesellschaft m.b.H.) - initial API and implementation
 *  Christian Voller (Mayr-Melnhof Karton Gesellschaft m.b.H.) - initial API and implementation
 *  CoSMIT GmbH - publishing, maintenance
 *******************************************************************************/

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.internal.ui.dialogs.ExceptionHandler;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BidiSegmentEvent;
import org.eclipse.swt.custom.BidiSegmentListener;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import com.mmkarton.mx7.reportgenerator.engine.MAXIMOReportDesignerUtil;
import com.mmkarton.mx7.reportgenerator.engine.SQLQuery;
import com.mmkarton.mx7.reportgenerator.provider.JdbcMetaDataProvider;
import com.mmkarton.mx7.reportgenerator.sqledit.SQLPartitionScanner;
import com.mmkarton.mx7.reportgenerator.sqledit.SQLSourceViewerConfiguration;
import com.mmkarton.mx7.reportgenerator.sqledit.SQLUtility;
import com.mmkarton.mx7.reportgenerator.util.ChildrenAllowedNode;
import com.mmkarton.mx7.reportgenerator.util.DBNodeUtil;
import com.mmkarton.mx7.reportgenerator.util.FilterConfig;
import com.mmkarton.mx7.reportgenerator.util.IDBNode;
import com.mmkarton.mx7.reportgenerator.util.RootNode;
import com.mmkarton.mx7.reportgenerator.util.FilterConfig.Type;


public class BIRTSQLWizardPage extends WizardPage 
{
	private Document doc = null;
	private Tree availableDbObjectsTree = null;
	//private Button identifierQuoteStringCheckBox = null;
	//private Button showSystemTableCheckBox = null;
	private Button includeSchemaCheckBox = null;
	private Button addDataSetCheckBox = null;
	private Combo schemaCombo = null;
	private int maxSchemaCount = 10;
	private int maxTableCountPerSchema = Integer.MAX_VALUE;
	boolean prefetchSchema = true;
	
	private Text searchTxt = null;
	private ComboViewer filterComboViewer = null;

	private Label schemaLabel = null;
	
	private FilterConfig fc;
	
	//private Text QueryText;
	
	private String sqlQueryText="";
	
	private SourceViewer viewer = null;
	
	public BIRTSQLWizardPage(String name) {
		super(MAXIMOReportDesignerUtil.titleName);
		setTitle(MAXIMOReportDesignerUtil.titleName);
		setDescription(MAXIMOReportDesignerUtil.titleName);
	}
	
	public String getQueryText() 
	{
		return viewer.getTextWidget().getText();
	}
	
	public void closeConnection()
	{
		JdbcMetaDataProvider.release();
	}

	public void createControl(Composite parent) 
	{
		prepareJDBCMetaDataProvider(  );
		Composite pageContainer = new Composite( parent, SWT.NONE );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 2;
		pageContainer.setLayout( layout );
		pageContainer.setLayoutData( new GridData( GridData.FILL_BOTH ) );

		Control left = createDBMetaDataSelectionComposite( pageContainer );
		Sash sash = createSash( pageContainer );
		Control right = createTextualQueryComposite( pageContainer );
		setWidthHints( pageContainer, left, right, sash );
		addDragListerner( sash, pageContainer, left, right );
		//return pageContainer;
		setControl(pageContainer);
		initializeControl();
		
	}
	
	private Control createDBMetaDataSelectionComposite( Composite parent )
	{
		boolean supportsSchema = JdbcMetaDataProvider.getInstance( ).isSupportSchema( );
		boolean supportsProcedure = JdbcMetaDataProvider.getInstance( ).isSupportProcedure( );
		Composite tablescomposite = new Composite( parent, SWT.NONE );
		GridLayout layout = new GridLayout( );

		tablescomposite.setLayout( layout );
		GridData data = new GridData( GridData.FILL_VERTICAL );
		data.grabExcessVerticalSpace = true;
		tablescomposite.setLayoutData( data );

		// Available Items
		Label dataSourceLabel = new Label( tablescomposite, SWT.LEFT );
		dataSourceLabel.setText( "Available Items"  );//$NON-NLS-1$
		dataSourceLabel.setText("Text" );//$NON-NLS-1$
		GridData labelData = new GridData( );
		dataSourceLabel.setLayoutData( labelData );

		availableDbObjectsTree = new Tree( tablescomposite, SWT.BORDER
				| SWT.MULTI );
		GridData treeData = new GridData( GridData.FILL_BOTH );
		treeData.grabExcessHorizontalSpace = true;
		treeData.grabExcessVerticalSpace = true;
		treeData.heightHint = 150;
		availableDbObjectsTree.setLayoutData( treeData );

		availableDbObjectsTree.addMouseListener( new MouseAdapter( ) {

			public void mouseDoubleClick( MouseEvent e )
			{
				String text = getTextToInsert( );
				if ( text.length( ) > 0 )
				{
					insertText( text );
				}
			}
		} );

		createSchemaFilterComposite( supportsSchema,supportsProcedure,tablescomposite );
		
		createSQLOptionGroup( tablescomposite );

		addDragSupportToTree( );
		addFetchDbObjectListener( );
		return tablescomposite;
	}
	
	private Sash createSash( final Composite composite )
	{
		final Sash sash = new Sash( composite, SWT.VERTICAL );
		sash.setLayoutData( new GridData( GridData.FILL_VERTICAL ) );
		return sash;
	}
	
	private Control createTextualQueryComposite( Composite parent )
	{

		Composite composite = new Composite( parent, SWT.FILL
				| SWT.LEFT_TO_RIGHT );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 1;
		composite.setLayout( layout );
		composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

		CompositeRuler ruler = new CompositeRuler( );
		LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn( );
		ruler.addDecorator( 0, lineNumbers );
		viewer = new SourceViewer( composite, ruler, SWT.H_SCROLL
				| SWT.V_SCROLL );
		
		SourceViewerConfiguration svc = new SQLSourceViewerConfiguration( null );
		//SourceViewerConfiguration svc = new SourceViewerConfiguration();
		viewer.configure( svc );

		//doc = new Document( getQueryText( ) );
		doc = new Document( "select \n from " );
		
		FastPartitioner partitioner = new FastPartitioner( new SQLPartitionScanner( ),
				new String[]{
						SQLPartitionScanner.QUOTE_STRING,
						SQLPartitionScanner.COMMENT,
						IDocument.DEFAULT_CONTENT_TYPE
				} );
		partitioner.connect( doc );
		doc.setDocumentPartitioner( partitioner );
		viewer.setDocument( doc );
		viewer.getTextWidget( ).setFont( JFaceResources.getTextFont( ) );
		viewer.getTextWidget( )
				.addBidiSegmentListener( new BidiSegmentListener( ) {

					/*
					 * @see
					 * org.eclipse.swt.custom.BidiSegmentListener#lineGetSegments
					 * (org.eclipse.swt.custom.BidiSegmentEvent)
					 */
					public void lineGetSegments( BidiSegmentEvent event )
					{
						event.segments = SQLUtility.getBidiLineSegments( event.lineText );
					}
				} );
		attachMenus( viewer );

		GridData data = new GridData( GridData.FILL_BOTH );
		data.widthHint = 500;
		viewer.getControl( ).setLayoutData( data );

		// Add drop support to the viewer
		addDropSupportToViewer( );

		// add support of additional accelerated key
		viewer.getTextWidget( ).addKeyListener( new KeyListener( ) {

			public void keyPressed( KeyEvent e )
			{
				if ( isUndoKeyPress( e ) )
				{
					viewer.doOperation( ITextOperationTarget.UNDO );
				}
				else if ( isRedoKeyPress( e ) )
				{
					viewer.doOperation( ITextOperationTarget.REDO );
				}
			}

			private boolean isUndoKeyPress( KeyEvent e )
			{
				// CTRL + z
				return ( ( e.stateMask & SWT.CONTROL ) > 0 )
						&& ( ( e.keyCode == 'z' ) || ( e.keyCode == 'Z' ) );
			}

			private boolean isRedoKeyPress( KeyEvent e )
			{
				// CTRL + y
				return ( ( e.stateMask & SWT.CONTROL ) > 0 )
						&& ( ( e.keyCode == 'y' ) || ( e.keyCode == 'Y' ) );
			}

			public void keyReleased( KeyEvent e )
			{
			}
		} );
		return composite;
	}
	
	private void setWidthHints( Composite pageContainer, Control left,
			Control right, Sash sash )
	{
		int leftWidth = left.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x;
		int totalWidth = pageContainer.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x;

		if ( (double) leftWidth / (double) totalWidth > 0.4 )
		{
			// if left side is too wide, set it to default value 40:60
			totalWidth = leftWidth / 40 * 100;
			leftWidth = leftWidth
					- sash.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x;
			GridData data = (GridData) left.getLayoutData( );
			data.widthHint = leftWidth;
			data = (GridData) right.getLayoutData( );
			data.widthHint = (int) ( totalWidth * 0.6 );
		}
		else
		{
			GridData data = (GridData) left.getLayoutData( );
			data.widthHint = leftWidth;
			data = (GridData) right.getLayoutData( );
			data.widthHint = totalWidth - leftWidth;
		}
	}
	
	private void addDragListerner( final Sash sash, final Composite parent,
			final Control left, final Control right )
	{
		sash.addListener( SWT.Selection, new Listener( ) {

			public void handleEvent( Event event )
			{
				if ( event.detail == SWT.DRAG )
				{
					return;
				}
				Sash sash = (Sash) event.widget;
				int shift = event.x - sash.getBounds( ).x;

				left.setSize( left.getSize( ).x + shift, left.getSize( ).y );
				right.setSize( right.getSize( ).x - shift, right.getSize( ).y );
				right.setLocation( right.getLocation( ).x + shift,
						right.getLocation( ).y );
				sash.setLocation( sash.getLocation( ).x + shift,
						sash.getLocation( ).y );
			}
		} );
	}
	
	private String getTextToInsert( )
	{
		TreeItem[] selection = availableDbObjectsTree.getSelection( );
		StringBuffer data = new StringBuffer( );
		if ( selection != null && selection.length > 0 )
		{
			for ( int i = 0; i < selection.length; i++ )
			{
				IDBNode dbNode = (IDBNode) selection[i].getData( );
				String sql = dbNode.getQualifiedNameInSQL(includeSchemaCheckBox.getSelection( ) );
				if ( sql != null )
				{
					data.append( sql ).append( "," );
				}
			}
		}
		String result = data.toString( );
		if ( result.length( ) > 0 )
		{
			// remove the last ","
			result = result.substring( 0, result.length( ) - 1 );
		}
		return result;
		
	}
	
	private void createSQLOptionGroup( Composite tablescomposite )
	{
		Group sqlOptionGroup = new Group( tablescomposite, SWT.FILL );
		GridLayout sqlOptionGroupLayout = new GridLayout( );
		sqlOptionGroupLayout.verticalSpacing = 10;
		sqlOptionGroup.setLayout( sqlOptionGroupLayout );
		GridData sqlOptionGroupData = new GridData( GridData.FILL_HORIZONTAL );
		sqlOptionGroup.setLayoutData( sqlOptionGroupData );

		//setupIdentifierQuoteStringCheckBox( sqlOptionGroup );
		
		setupIncludeSchemaCheckBox( sqlOptionGroup );
		setupAddNewDataSetCheckBox(sqlOptionGroup);
	}
	
	/*private void setupIdentifierQuoteStringCheckBox( Group group )
	{
		GridData layoutData = new GridData( GridData.FILL_HORIZONTAL );
		layoutData.horizontalSpan = 3;
		identifierQuoteStringCheckBox = new Button( group, SWT.CHECK );
		identifierQuoteStringCheckBox.setText( "Quote" ); //$NON-NLS-1$
		identifierQuoteStringCheckBox.setSelection( false );
		identifierQuoteStringCheckBox.setLayoutData( layoutData );
	}

	
	private void setupShowSystemTableCheckBox( Group group )
	{
		GridData layoutData = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING);
		layoutData.horizontalSpan = 2;
		showSystemTableCheckBox = new Button( group, SWT.CHECK );
		showSystemTableCheckBox.setText( "Show System Tables" ); //$NON-NLS-1$
		showSystemTableCheckBox.setSelection( false );
		showSystemTableCheckBox.setLayoutData( layoutData );
		showSystemTableCheckBox.setEnabled( false );
	}*/
	
	/**
	 * 
	 * @param group
	 */
	private void setupIncludeSchemaCheckBox( Group group )
	{
		GridData layoutData = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING );
		layoutData.horizontalSpan = 2;
		includeSchemaCheckBox = new Button( group, SWT.CHECK );
		includeSchemaCheckBox.setText( "Qualified Expression" ); //$NON-NLS-1$
		includeSchemaCheckBox.setSelection( false );
		includeSchemaCheckBox.setLayoutData( layoutData );
		includeSchemaCheckBox.setEnabled( true );
	}
	
	private void setupAddNewDataSetCheckBox( Group group )
	{
		GridData layoutData = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING );
		layoutData.horizontalSpan = 3;
		addDataSetCheckBox = new Button( group, SWT.CHECK );
		addDataSetCheckBox.setText( "Add another DataSet" ); //$NON-NLS-1$
		addDataSetCheckBox.setSelection( false );
		addDataSetCheckBox.setLayoutData( layoutData );
		addDataSetCheckBox.setEnabled( true );
	}
	
	
	private void addDragSupportToTree( )
	{
		DragSource dragSource = new DragSource( availableDbObjectsTree,
				DND.DROP_COPY );
		dragSource.setTransfer( new Transfer[]{
			TextTransfer.getInstance( )
		} );
		dragSource.addDragListener( new DragSourceAdapter( ) {

			private String textToInsert;
			
			public void dragStart( DragSourceEvent event )
			{
				event.doit = false;
				this.textToInsert = getTextToInsert( );
				if ( textToInsert.length( ) > 0 )
				{
					event.doit = true;
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.dnd.DragSourceAdapter#dragSetData(org.eclipse
			 * .swt.dnd.DragSourceEvent)
			 */
			public void dragSetData( DragSourceEvent event )
			{
				if ( TextTransfer.getInstance( )
						.isSupportedType( event.dataType ) )
				{
					event.data = textToInsert;
				}
			}
		} );
	}
	
	private void addFetchDbObjectListener( )
	{

		availableDbObjectsTree.addListener( SWT.Expand, new Listener( ) {

			/*
			 * @see
			 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.
			 * widgets.Event)
			 */
			public void handleEvent( final Event event )
			{
				TreeItem item = (TreeItem) event.item;
				BusyIndicator.showWhile( item.getDisplay( ), new Runnable( ) {

					/*
					 * @see java.lang.Runnable#run()
					 */
					public void run( )
					{
						listChildren( event );
					}
				} );
			}

			/**
			 * @param event
			 */
			private void listChildren( Event event )
			{
				TreeItem item = (TreeItem) event.item;
				
				IDBNode node = (IDBNode) item.getData( );
				if ( node instanceof ChildrenAllowedNode )
				{
					ChildrenAllowedNode parent = (ChildrenAllowedNode) node;
					if ( !parent.isChildrenPrepared( ) )
					{
						item.removeAll( );
						parent.prepareChildren( fc );
						if ( parent.getChildren( ) != null )
						{
							for ( IDBNode child : parent.getChildren( ) )
							{
								DBNodeUtil.createTreeItem( item, child );
							}
						}
					}
				}
			}
		} );
	}
	
	private final void attachMenus( SourceViewer viewer )
	{
		StyledText widget = viewer.getTextWidget( );
		//TextMenuManager menuManager = new TextMenuManager( viewer );
		//widget.setMenu( menuManager.getContextMenu( widget ) );
	}
	
	private void addDropSupportToViewer( )
	{
		final StyledText text = viewer.getTextWidget( );
		DropTarget dropTarget = new DropTarget( text, DND.DROP_COPY
				| DND.DROP_DEFAULT );
		dropTarget.setTransfer( new Transfer[]{
			TextTransfer.getInstance( )
		} );
		dropTarget.addDropListener( new DropTargetAdapter( ) {

			public void dragEnter( DropTargetEvent event )
			{
				text.setFocus( );
				if ( event.detail == DND.DROP_DEFAULT )
					event.detail = DND.DROP_COPY;
				if ( event.detail != DND.DROP_COPY )
					event.detail = DND.DROP_NONE;
			}

			public void dragOver( DropTargetEvent event )
			{
				event.feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
			}

			public void dragOperationChanged( DropTargetEvent event )
			{
				dragEnter( event );
			}

			public void drop( DropTargetEvent event )
			{
				if ( event.data instanceof String && !event.data.equals( "" ) )
					insertText( (String) event.data );
			}
		} );
	}
	private void insertText( String text )
	{
		if ( text == null )
			return;

		StyledText textWidget = viewer.getTextWidget( );
		int selectionStart = textWidget.getSelection( ).x;
		textWidget.insert( text );
		textWidget.setSelection( selectionStart + text.length( ) );
		textWidget.setFocus( );
		sqlQueryText=textWidget.getText();
	}	
	
	private void createSchemaFilterComposite( boolean supportsSchema,
			boolean supportsProcedure, Composite tablescomposite )
	{
		// Group for selecting the Tables etc
		// Searching the Tables and Views
		Group selectTableGroup = new Group( tablescomposite, SWT.FILL );

		GridLayout groupLayout = new GridLayout( );
		groupLayout.numColumns = 3;
		// groupLayout.horizontalSpacing = 10;
		groupLayout.verticalSpacing = 10;
		selectTableGroup.setLayout( groupLayout );

		GridData selectTableData = new GridData( GridData.FILL_HORIZONTAL );
		selectTableGroup.setLayoutData( selectTableData );

		schemaLabel = new Label( selectTableGroup, SWT.LEFT );
		schemaLabel.setText( "Schema"  );

		schemaCombo = new Combo( selectTableGroup, prefetchSchema
				? SWT.READ_ONLY : SWT.DROP_DOWN );

		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		schemaCombo.setLayoutData( gd );

		Label FilterLabel = new Label( selectTableGroup, SWT.LEFT );
		FilterLabel.setText( "Filter"  );

		searchTxt = new Text( selectTableGroup, SWT.BORDER );
		GridData searchTxtData = new GridData( GridData.FILL_HORIZONTAL );
		searchTxtData.horizontalSpan = 2;
		searchTxt.setLayoutData( searchTxtData );

		// Select Type
		Label selectTypeLabel = new Label( selectTableGroup, SWT.NONE );
		selectTypeLabel.setText( "Selecttype"  );

		// Filter Combo
		filterComboViewer = new ComboViewer( selectTableGroup, SWT.READ_ONLY );
		setFilterComboContents( filterComboViewer, supportsProcedure );
		GridData filterData = new GridData( GridData.FILL_HORIZONTAL );
		filterData.horizontalSpan = 2;
		filterComboViewer.getControl( ).setLayoutData( filterData );

		//setupShowSystemTableCheckBox( selectTableGroup );

		// Find Button
		Button findButton = new Button( selectTableGroup, SWT.NONE );
		GridData btnData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER );
		btnData.horizontalSpan = 3;
		findButton.setLayoutData( btnData );
		
		findButton.setText( "Filter"  );//$NON-NLS-1$

		// Add listener to the find button
		findButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent event )
			{
				PlatformUI.getWorkbench( )
						.getDisplay( )
						.asyncExec( new Runnable( ) {

							public void run( )
							{
								fc = populateFilterConfig( );
								DBNodeUtil.createTreeRoot( availableDbObjectsTree,
										new RootNode( "MaximoDB" ),
										fc );
							}
						} );
				
			}
		} );
		
		String[] allSchemaNames = null;
		String[] defaultSchemaName = null;
		
		if ( supportsSchema )
		{
			String allFlag = "All" ;
			schemaCombo.add( allFlag );

			if ( prefetchSchema )
			{
				allSchemaNames = JdbcMetaDataProvider.getInstance( )
						.getMaximoSchema();
				
				String defaultSchema=JdbcMetaDataProvider.getInstance( ).getDefaultschema();
				List<String> defaultnames = new ArrayList<String>( );

				for ( String name : allSchemaNames )
				{
							schemaCombo.add( name );
							if(name.equalsIgnoreCase(defaultSchema))
								defaultnames.add(name);
							
				}
				defaultSchemaName=defaultnames.toArray( new String[0] );
			}
			schemaCombo.select( 0 );
		}
		else
		{
			schemaCombo.removeAll( );
			schemaCombo.setEnabled( false );
			schemaLabel.setEnabled( false );
		}
		if ( prefetchSchema )
		{
			fc = populateFilterConfig( );
			DBNodeUtil.createTreeRoot( availableDbObjectsTree,
					new RootNode( "MaximoDB", defaultSchemaName ),
					fc );
		}
		else
		{
			DBNodeUtil.createRootTip( availableDbObjectsTree,
					new RootNode( "MaximoDB") );
		}
	}
	
	private void prepareJDBCMetaDataProvider(  )
	{
		
		try
		{
			JdbcMetaDataProvider.createInstance();
			JdbcMetaDataProvider.getInstance( ).reconnect( );
		}
		catch ( Exception e )
		{
			ExceptionHandler.showException( PlatformUI.getWorkbench( )
					.getDisplay( )
					.getActiveShell( ),
					"Error" ,
					e.getLocalizedMessage( ),
					e );
		}
	}
	
	private void initializeControl( )
	{
		String DEFAULT_MESSAGE = "Query" ;
		setMessage( DEFAULT_MESSAGE, IMessageProvider.NONE );
		viewer.getTextWidget( ).setFocus( );
	}
	
	private void setFilterComboContents( ComboViewer filterComboViewer,
			boolean supportsProcedure )
	{
		if ( filterComboViewer == null )
		{
			return;
		}

		List<FilterConfig.Type> types = new ArrayList<FilterConfig.Type>( );

		// Populate the Types of Data bases objects which can be retrieved
		types.add( Type.ALL );
		types.add( Type.TABLE );
		types.add( Type.VIEW );
		if ( supportsProcedure )
		{
			types.add( Type.PROCEDURE );
		}
		filterComboViewer.setContentProvider( new IStructuredContentProvider( ) {

			@SuppressWarnings("unchecked")
			public Object[] getElements( Object inputElement )
			{
				return ( (List) inputElement ).toArray( );
			}

			public void dispose( )
			{
			}

			public void inputChanged( Viewer viewer, Object oldInput,
					Object newInput )
			{
			}

		} );

		filterComboViewer.setLabelProvider( new LabelProvider( ) {

			public String getText( Object inputElement )
			{
				FilterConfig.Type type = (FilterConfig.Type) inputElement;
				return FilterConfig.getTypeDisplayText( type );
			}

		} );

		filterComboViewer.setInput( types );

		// Set the Default selection to the First Item , which is "All"
		filterComboViewer.getCombo( ).select( 0 );
		filterComboViewer.getCombo( )
				.addSelectionListener( new SelectionAdapter( ) {

					public void widgetSelected( SelectionEvent e )
					{
						Type type = getSelectedFilterType( );
						/*if ( type == Type.ALL || type == Type.TABLE )
						{
							showSystemTableCheckBox.setEnabled( true );
						}
						else
						{
							showSystemTableCheckBox.setEnabled( false );
						}*/
					}
				} );
	}

	/**
	 * 
	 * @return The Type of the object selected in the type combo
	 */
	private FilterConfig.Type getSelectedFilterType( )
	{
		IStructuredSelection selection = (IStructuredSelection) filterComboViewer.getSelection( );
		FilterConfig.Type type = Type.ALL;
		if ( selection != null && selection.getFirstElement( ) != null )
		{
			return (Type) selection.getFirstElement( );
		}
		return type;
	}
	private FilterConfig populateFilterConfig( )
	{
		String schemaName = null;
		if ( schemaCombo.isEnabled( ) && schemaCombo.getSelectionIndex( ) != 0 )
		{
			schemaName = schemaCombo.getText( );
		}
		Type type = getSelectedFilterType( );
		String namePattern = searchTxt.getText( );
		boolean isShowSystemTable = false;//showSystemTableCheckBox.getSelection( );
		FilterConfig result = new FilterConfig( schemaName,
				type,
				namePattern,
				isShowSystemTable,
				maxSchemaCount,
				maxTableCountPerSchema );
		return result;
	}

	public SQLQuery getSQLQuery() 
	{
		SQLQuery query=new SQLQuery();
		
		
		return null;
	}

}
