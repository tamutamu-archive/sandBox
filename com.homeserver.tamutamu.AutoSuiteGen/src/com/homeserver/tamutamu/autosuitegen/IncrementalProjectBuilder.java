package com.homeserver.tamutamu.autosuitegen;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class IncrementalProjectBuilder extends
		org.eclipse.core.resources.IncrementalProjectBuilder {

	public static final String BUILDER_ID = Activator.PLUGIN_ID + ".Builder";

	public IncrementalProjectBuilder() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {


		IProject project = getProject();
	    IResourceDelta delta = getDelta(project);
	    if (delta != null) {
	        taskDelta(delta);
	    } else {
	        taskResource(project);
	    }
	    return new IProject[] { project };

	}

	private void taskResource(IResource resource) throws CoreException {
	    if (resource instanceof IContainer) {
	        IContainer container = (IContainer)resource;
	        for (IResource child : container.members()) {
	            taskResource(child);
	        }
	    } else {
	        // some task with child...
	    }
	}

	private void taskDelta(IResourceDelta delta) throws CoreException {
	    if (delta == null) {
	        return;
	    }
	    IResourceDelta[] children = delta.getAffectedChildren();
	    for (IResourceDelta child : children) {
	        // some task with child.getResource() ...
	        taskDelta(child);
	    }
	}

}
