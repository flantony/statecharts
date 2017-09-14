/**
 * Copyright (c) 2012-2017 committers of YAKINDU and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     committers of YAKINDU - initial API and implementation
 */
package org.yakindu.sct.model.sgraph.validation;

import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import com.google.inject.Inject;

public abstract class AbstractSGraphJavaValidator extends AbstractDeclarativeValidator{
	@Override
	@Inject
	public void register(EValidatorRegistrar registrar) {
		// Do not register because this validator is only a composite #398987
	}
}
