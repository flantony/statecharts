/**
 * Copyright (c) 2011 committers of YAKINDU and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * 	committers of YAKINDU - initial API and implementation
 * 
 */
package org.yakindu.sct.simulation.ui.view;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.yakindu.sct.simulation.core.debugmodel.SCTDebugTarget;

/**
 * Base {@link ViewPart} implementation for all views that are related to the
 * current selected debug target.
 * 
 * @author andreas muelder - Initial contribution and API
 * 
 */
public abstract class AbstractDebugTargetView extends ViewPart
		implements
			IDebugEventSetListener,
			IDebugContextListener,
			ILaunchListener {

	protected IDebugTarget debugTarget;

	protected abstract void activeTargetChanged(IDebugTarget target);

	protected abstract void handleDebugEvent(DebugEvent event);

	public AbstractDebugTargetView() {
		registerListeners();
	}

	protected void registerListeners() {
		DebugUITools.getDebugContextManager().addDebugContextListener(this);
		DebugPlugin.getDefault().addDebugEventListener(this);
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
	}

	@Override
	public void dispose() {
		DebugUITools.getDebugContextManager().removeDebugContextListener(this);
		DebugPlugin.getDefault().removeDebugEventListener(this);
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		setActiveSession();
	}

	protected void setActiveSession() {
		// if a simulation session is running, we should initialize with its
		// content
		IAdaptable debugContext = DebugUITools.getDebugContext();
		if (debugContext != null) {
			IDebugTarget debugTarget = (IDebugTarget) debugContext.getAdapter(IDebugTarget.class);
			if (debugTarget != null) {
				if (!debugTarget.isTerminated()) {
					this.debugTarget = (IDebugTarget) debugTarget;
					activeTargetChanged(this.debugTarget);
				}
			}
		}
	}

	public void debugContextChanged(DebugContextEvent event) {
		if ((event.getFlags() & DebugContextEvent.ACTIVATED) > 0) {
			PlatformObject object = (PlatformObject) ((IStructuredSelection) event.getContext()).getFirstElement();
			if (object == null)
				return;
			IDebugTarget newTarget = null;
			if (object instanceof Launch) {
				newTarget = ((Launch) object).getDebugTarget();
			} else {
				newTarget = (IDebugTarget) object.getAdapter(IDebugTarget.class);
			}
			changeTarget(newTarget);
		}
	}

	private void changeTarget(IDebugTarget newTarget) {
		if (newTarget == debugTarget) {
			return;
		}
		if (newTarget != debugTarget && newTarget != null && !newTarget.isTerminated()) {
			debugTarget = newTarget;
			activeTargetChanged(newTarget);
		} else {
			setActiveSession();
		}
	}

	public void launchAdded(ILaunch launch) {
		// do nothing - already handled by debugevents
	}

	@Override
	public void launchRemoved(ILaunch launch) {
		// do nothing - already handled by debugevents
	}

	@Override
	public void launchChanged(ILaunch launch) {
		for (IDebugTarget newTarget : launch.getDebugTargets()) {
			changeTarget(newTarget);
		}
	}

	public final void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent debugEvent : events) {
			// Only notify about events fired from the active debug target
			if ((debugEvent.getSource() instanceof SCTDebugTarget) && debugEvent.getSource() == debugTarget)
				handleDebugEvent(debugEvent);
		}
	}
}
