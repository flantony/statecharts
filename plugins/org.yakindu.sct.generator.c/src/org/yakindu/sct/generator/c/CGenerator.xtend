/**
 * Copyright (c) 2012 committers of YAKINDU and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * 	committers of YAKINDU - initial API and implementation
 * 
 */
package org.yakindu.sct.generator.c

import com.google.inject.Inject
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.generator.core.impl.IExecutionFlowGenerator
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.model.sgraph.Statechart

/**
 * This is the C code generators main class. 
 * 
 * @author Axel Terfloth
 */
class CGenerator implements IExecutionFlowGenerator {
	 
	@Inject extension Types
	@Inject extension StatemachineHeader
	@Inject extension StatemachineSource
	@Inject extension StatemachineRequiredHeader
	@Inject extension Navigation
	@Inject extension GenmodelEntries
	@Inject extension Naming
	
	/**
	 * @Deprecated use {@link #generate(ExecutionFlow, GeneratorEntry, IFileSystemAccess, ArtifactLocationProvider)} instead
	 */
	@Deprecated
	override generate(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa) {
		generate(flow, entry, fsa, new ArtifactLocationProvider())
	}
	
	def generate(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa, ArtifactLocationProvider locations) {
		initArtifactLocations(flow, entry, locations)
		
		flow.generateTypesH(flow.sourceElement as Statechart, fsa, entry)
		flow.generateStatemachineHeader(flow.sourceElement as Statechart, fsa, entry, locations)
		if (flow.timed || !flow.operations.empty || entry.tracingEnterState || entry.tracingExitState) {
			flow.generateStatemachineRequiredHeader(flow.sourceElement as Statechart, fsa, entry)
		}
		flow.generateStatemachineSource(flow.sourceElement as Statechart, fsa, entry)
	}
	
	def protected initArtifactLocations(ExecutionFlow flow, GeneratorEntry entry, ArtifactLocationProvider locations) {
		locations.addArtifact(flow.typesModule.h, IExecutionFlowGenerator.LIBRARY_TARGET_FOLDER_OUTPUT)
		locations.addArtifact(flow.module.h, IExecutionFlowGenerator.TARGET_FOLDER_OUTPUT)
		locations.addArtifact(flow.module.c, IExecutionFlowGenerator.TARGET_FOLDER_OUTPUT)
		if (flow.timed || !flow.operations.empty || entry.tracingEnterState || entry.tracingExitState) {
			locations.addArtifact(flow.module.client.h, IExecutionFlowGenerator.TARGET_FOLDER_OUTPUT)
		}
	}
	
}