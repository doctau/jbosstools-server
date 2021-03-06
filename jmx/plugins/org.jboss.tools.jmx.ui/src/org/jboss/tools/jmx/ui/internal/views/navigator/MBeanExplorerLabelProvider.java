/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
 *******************************************************************************/

package org.jboss.tools.jmx.ui.internal.views.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.MBeanAttributeInfoWrapper;
import org.jboss.tools.jmx.core.MBeanInfoWrapper;
import org.jboss.tools.jmx.core.MBeanOperationInfoWrapper;
import org.jboss.tools.jmx.core.tree.DomainNode;
import org.jboss.tools.jmx.core.tree.ErrorRoot;
import org.jboss.tools.jmx.core.tree.ObjectNameNode;
import org.jboss.tools.jmx.core.tree.PropertyNode;
import org.jboss.tools.jmx.ui.Messages;
import org.jboss.tools.jmx.ui.UIExtensionManager;
import org.jboss.tools.jmx.ui.UIExtensionManager.ConnectionProviderUI;
import org.jboss.tools.jmx.ui.internal.JMXImages;
import org.jboss.tools.jmx.ui.internal.MBeanUtils;
import org.jboss.tools.jmx.ui.internal.views.navigator.MBeanExplorerContentProvider.DelayProxy;

/**
 * Label Provider for the view
 */
public class MBeanExplorerLabelProvider extends LabelProvider {
	private static ArrayList<MBeanExplorerLabelProvider> instances =
		new ArrayList<MBeanExplorerLabelProvider>();
	private static HashMap<String, Image> images =
		new HashMap<String, Image>();

	public MBeanExplorerLabelProvider() {
		super();
		instances.add(this);
	}

    public void dispose() {
		instances.remove(this);
		if( instances.isEmpty()) {
			Iterator<Image> i = images.values().iterator();
	    	while(i.hasNext()) {
	    		Image o = i.next();
	    		if( o != null )
	    			o.dispose();
	    	}
		}
    	super.dispose();
    }

	public String getText(Object obj) {
		return MBeanExplorerLabelProvider.getText2(obj);
	}
	
	public static String getText2(Object obj) {
		if( obj instanceof IConnectionWrapper ) {
			IConnectionProvider provider = ((IConnectionWrapper)obj).getProvider();
			return provider.getName((IConnectionWrapper)obj);
		}

		if( obj instanceof DelayProxy ) {
			return Messages.Loading;
		}
        if( obj instanceof ErrorRoot ) {
        	return Messages.ErrorLoading;
        }
		
		if (obj instanceof DomainNode) {
			DomainNode node = (DomainNode) obj;
			return node.getDomain();
		}
		if (obj instanceof ObjectNameNode) {
			ObjectNameNode node = (ObjectNameNode) obj;
			return node.getValue();
		}
		if (obj instanceof PropertyNode) {
			PropertyNode node = (PropertyNode) obj;
			return node.getValue();
		}
		if (obj instanceof MBeanInfoWrapper) {
			MBeanInfoWrapper wrapper = (MBeanInfoWrapper) obj;
			return wrapper.getObjectName().toString();
		}
		if (obj instanceof MBeanOperationInfoWrapper) {
			MBeanOperationInfoWrapper wrapper = (MBeanOperationInfoWrapper) obj;
			return MBeanUtils.prettySignature(wrapper.getMBeanOperationInfo());
		}
		if (obj instanceof MBeanAttributeInfoWrapper) {
			MBeanAttributeInfoWrapper wrapper = (MBeanAttributeInfoWrapper) obj;
			return wrapper.getMBeanAttributeInfo().getName();
		}
		return obj.toString();
	}

	@Override
	public Image getImage(Object obj) {
		if( obj instanceof IConnectionWrapper ) {
			IConnectionProvider provider = ((IConnectionWrapper)obj).getProvider();
			ConnectionProviderUI ui = UIExtensionManager.getConnectionProviderUI(provider.getId());
			if( ui != null && ui.getId() != null ) {
				if(images.get(ui.getId()) == null || images.get(ui.getId()).isDisposed()) {
					Image i = null;
					if( ui.getImageDescriptor() != null ) {
						i = ui.getImageDescriptor().createImage();
					}
					images.put(ui.getId(), i);
				}
				return images.get(ui.getId());
			}
		}
		if( obj instanceof DelayProxy ) {
			return null;
		}
        if( obj instanceof ErrorRoot ) {
        	return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
        }

		if (obj instanceof DomainNode) {
			return JMXImages.get(JMXImages.IMG_OBJS_LIBRARY);
		}
		if (obj instanceof ObjectNameNode) {
			return JMXImages.get(JMXImages.IMG_OBJS_METHOD);
		}
		if (obj instanceof PropertyNode) {
			return JMXImages.get(JMXImages.IMG_OBJS_PACKAGE);
		}
		if (obj instanceof MBeanInfoWrapper) {
			return JMXImages.get(JMXImages.IMG_OBJS_METHOD);
		}
		if (obj instanceof MBeanAttributeInfoWrapper) {
			return JMXImages.get(JMXImages.IMG_FIELD_PUBLIC);
		}
		if (obj instanceof MBeanOperationInfoWrapper) {
			return JMXImages.get(JMXImages.IMG_MISC_PUBLIC);
		}
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

}