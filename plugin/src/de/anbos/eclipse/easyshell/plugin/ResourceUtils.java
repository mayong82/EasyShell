/*******************************************************************************
 * Copyright (c) 2014 - 2018 Andre Bossert.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Bossert - initial API and implementation and/or initial documentation
 *******************************************************************************/

package de.anbos.eclipse.easyshell.plugin;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.osgi.framework.Bundle;
import org.eclipse.cdt.internal.core.model.ExternalTranslationUnit;
import org.eclipse.cdt.internal.core.model.IncludeReference;
import org.eclipse.cdt.internal.ui.cview.IncludeReferenceProxy;
import org.eclipse.cdt.internal.ui.includebrowser.IBNode;
import org.eclipse.cdt.core.model.IIncludeReference;


@SuppressWarnings("restriction")
public class ResourceUtils {

    static public ISelection getResourceSelection(IWorkbenchPart part) {
        ISelection selection = null;
        if (part != null) {
            if (part instanceof IEditorPart) {
                Resource file = getResource((IEditorPart)part);
                if (file != null) {
                    selection = new StructuredSelection(file);
                }
            } else {
                try {
                    selection = part.getSite().getSelectionProvider().getSelection();
                } catch(Exception e) {
                    // no op
                }
            }
        }
        return selection;
    }

    static public Resource getResource(Object myObj) {
        Object object = null;

        // handle IEditorPart
        if (myObj instanceof IEditorPart) {
            IEditorPart editorPart = (IEditorPart)myObj;
            IEditorInput input = editorPart.getEditorInput();
            if (input instanceof IFileEditorInput) {
                object = ((IFileEditorInput)input).getFile();
            } else {
                Object adapter = input.getAdapter(IFile.class);
                if(adapter instanceof IFile){
                    object = (IFile) adapter;
                } else {
                    adapter = editorPart.getAdapter(IFile.class);
                    if(adapter instanceof IFile){
                        object = (IFile) adapter;
                    } else {
                        object = input;
                    }
                }
            }
        } else {
            object = myObj;
        }

        // the most input types first
        if (object instanceof Resource) {
            return new Resource((Resource)object);
        }
        if (object instanceof IFile) {
            return new Resource(((IFile) object));
        }
        if (object instanceof File) {
            return new Resource((File) object);
        }

        // still adaptable
        if (object instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) object;
            // IFile
            IFile iFile = (IFile) adaptable.getAdapter(IFile.class);
            if (iFile != null) {
                return new Resource(iFile);
            }
            // IResource
            IResource ires = (IResource) adaptable.getAdapter(IResource.class);
            if (ires != null) {
                IPath path = ires.getLocation();
                if (path != null) {
                    return new Resource(ires);
                }
            }
            // FileStoreEditorInput
            if (adaptable instanceof FileStoreEditorInput) {
                URI fileuri = ((FileStoreEditorInput) adaptable).getURI();
                return new Resource(new File(fileuri.getPath()));
            }
            // optional org.eclipse.jdt.core
            Bundle bundle = Platform.getBundle("org.eclipse.jdt.core");
            if (bundle != null) {
                if ( (adaptable instanceof PackageFragment) &&
                     ( ((PackageFragment) adaptable).getPackageFragmentRoot() instanceof JarPackageFragmentRoot) ) {
                    return new Resource(getJarFile(((PackageFragment) adaptable)
                            .getPackageFragmentRoot()));
                } else if (adaptable instanceof JarPackageFragmentRoot) {
                    return new Resource(getJarFile(adaptable));
                }
            }
            // optional org.eclipse.cdt.core
            bundle = Platform.getBundle("org.eclipse.cdt.core");
            if (bundle != null) {
                IPath path = null;
                if (adaptable instanceof IncludeReferenceProxy) {
                    IIncludeReference includeRef = ((IncludeReferenceProxy) adaptable).getReference();
                    path = includeRef.getPath();
                } else if (adaptable instanceof IncludeReference) {
                    IIncludeReference includeRef = ((IncludeReference) adaptable);
                    path = includeRef.getPath();
                } else if (adaptable instanceof ExternalTranslationUnit) {
                    path = ((ExternalTranslationUnit) adaptable).getLocation();
                } else if (adaptable instanceof IBNode) {
                    path = ((IBNode) adaptable).getRepresentedPath();
                }
                if (path != null) {
                    return new Resource(path.toFile());
                }
            }
            // File
            File file = (File) adaptable.getAdapter(File.class);
            if (file != null) {
                return new Resource(file);
            }
        }
        return null;
    }

    static private File getJarFile(IAdaptable adaptable) {
        JarPackageFragmentRoot jpfr = (JarPackageFragmentRoot) adaptable;
        File file = (File)jpfr.getPath().makeAbsolute().toFile();
        if (!((File)file).exists()) {
            File projectFile = new File(jpfr.getJavaProject().getProject().getLocation().toOSString());
            file = new File(projectFile.getParent() + file.toString());
        }
        return file;
    }

}
