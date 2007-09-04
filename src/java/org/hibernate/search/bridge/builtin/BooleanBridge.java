//$Id$
package org.hibernate.search.bridge.builtin;

import org.hibernate.search.bridge.TwoWayStringBridge;
import org.hibernate.annotations.common.util.StringHelper;


/**
 * Map a boolean field
 *
 * @author Sylvain Vieujot
 */
public class BooleanBridge implements TwoWayStringBridge {

	public Boolean stringToObject(String stringValue) {
		if ( StringHelper.isEmpty(stringValue) ) return null;
		return Boolean.valueOf( stringValue );
	}

	public String objectToString(Object object) {
		return object == null ?
				null :
				( (Boolean) object ).toString();
	}
}
