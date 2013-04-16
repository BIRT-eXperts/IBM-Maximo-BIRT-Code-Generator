/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *  Ing. Gerd Stockner (Mayr-Melnhof Karton Gesellschaft m.b.H.) - modifications
 *  Christian Voller (Mayr-Melnhof Karton Gesellschaft m.b.H.) - modifications
 *  CoSMIT GmbH - publishing, maintenance
 *******************************************************************************/

package com.mmkarton.mx7.reportgenerator.sqledit;


import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.mmkarton.mx7.reportgenerator.util.ColorManager;

/**
 * TODO: Please document
 * 
 * @version $Revision: 1.3 $ $Date: 2008/08/21 09:42:14 $
 */

public class SQLSourceViewerConfiguration extends SourceViewerConfiguration
{
	private static final TextAttribute quoteString = new TextAttribute( ColorManager.getColor(42, 0, 255) ) ;
	private static final TextAttribute comment = new TextAttribute( ColorManager.getColor(63, 127, 95) ) ;
	private DataSourceDesign dsd;
	/**
	 *  
	 */
	public SQLSourceViewerConfiguration( DataSourceDesign dsd )
	{
		super( );
		this.dsd = dsd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer )
	{
		PresentationReconciler reconciler = new PresentationReconciler( );
		
		NonRuleBasedDamagerRepairer dr = new NonRuleBasedDamagerRepairer( quoteString );
		reconciler.setDamager( dr, SQLPartitionScanner.QUOTE_STRING );
		reconciler.setRepairer( dr, SQLPartitionScanner.QUOTE_STRING );
		
		
		dr = new NonRuleBasedDamagerRepairer( comment );
		reconciler.setDamager( dr, SQLPartitionScanner.COMMENT );
		reconciler.setRepairer( dr, SQLPartitionScanner.COMMENT );
		
		
		DefaultDamagerRepairer  ddr = new DefaultDamagerRepairer( new SQLKeywordScanner( ) );
		reconciler.setDamager( ddr, IDocument.DEFAULT_CONTENT_TYPE );
		reconciler.setRepairer( ddr, IDocument.DEFAULT_CONTENT_TYPE );

		return reconciler;
	}

	@Override
	public String[] getConfiguredContentTypes( ISourceViewer sourceViewer )
	{
		return new String[]{
				SQLPartitionScanner.QUOTE_STRING,
				SQLPartitionScanner.COMMENT,
				IDocument.DEFAULT_CONTENT_TYPE };
	}
	

	public IContentAssistant getContentAssistant( ISourceViewer sourceViewer )
	{
		ContentAssistant assistant = new ContentAssistant( );
		JdbcSQLContentAssistProcessor contentAssist = new JdbcSQLContentAssistProcessor( );
		contentAssist.setDataSourceHandle( dsd );
		assistant.setContentAssistProcessor( contentAssist,
				IDocument.DEFAULT_CONTENT_TYPE );
		assistant.enableAutoActivation( true );
		assistant.setAutoActivationDelay( 500 );
		assistant.setProposalPopupOrientation( IContentAssistant.PROPOSAL_OVERLAY );
		return assistant;
	}
}