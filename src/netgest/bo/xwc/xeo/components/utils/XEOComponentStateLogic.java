package netgest.bo.xwc.xeo.components.utils;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boApplication;

public class XEOComponentStateLogic {

	public static final boolean isMethodDisabled( boObject xeoObject, String methodName ) {
		try {
			if (xeoObject.isEnabled) {
				if (netgest.bo.security.securityRights.canExecute(xeoObject
						.getEboContext(), xeoObject.getName(), methodName)) {
					return false;
				}
				return true;
			}
			return true;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public static final boolean isMethodHidden( boObject xeoObject, String methodName ) {
		try {
			if (
				netgest.bo.security.securityRights.canWrite( xeoObject.getEboContext() ,xeoObject.getName() )
				&&
				!xeoObject.methodIsHidden( methodName )
			) {
				return false;
			}
			return true;
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}
	
	public static final boolean isBridgeAddEnabled( bridgeHandler bridgeHandler ) {
		try {
			return bridgeHandler.disableWhen() || !bridgeHandler.getParent().isEnabled;
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}

	public static final boolean isBridgeRemoveEnabled( bridgeHandler bridgeHandler ) {
		try {
			return bridgeHandler.disableWhen() || !bridgeHandler.getParent().isEnabled;
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}

	public static final boolean isBridgeRemoveNew( bridgeHandler bridgeHandler ) {
		try {
			return bridgeHandler.disableWhen() || !bridgeHandler.getParent().isEnabled;
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}
	
	public static final boolean isBridgeAddVisible( bridgeHandler bridgeHandler ) {
		try {
			if (netgest.bo.security.securityRights.canWrite( bridgeHandler.getEboContext() , bridgeHandler.getParent().getName() ) ) {
				boDefAttribute bridgeDef = bridgeHandler.getDefAttribute();
				if( bridgeDef.supportManualAdd() && bridgeDef.supportManualOperation() ) {
					if ( 
							netgest.bo.security.securityRights.canAdd( 
									bridgeHandler.getEboContext() ,
									bridgeHandler.getParent().getName(), 
									bridgeHandler.getName() 
							)
					)
					{
						return true;
					}
				}
			}
			return false;
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}

	public static final boolean isBridgeNewVisible( bridgeHandler bridgeHandler ) {
		try {
			if (netgest.bo.security.securityRights.canWrite( bridgeHandler.getEboContext() , bridgeHandler.getParent().getName() ) ) {
				boDefAttribute bridgeDef = bridgeHandler.getDefAttribute();
				if( bridgeDef.supportManualCreate() && bridgeDef.supportManualOperation() ) {
					if ( 
							netgest.bo.security.securityRights.canAdd( 
									bridgeHandler.getEboContext() ,
									bridgeHandler.getParent().getName(), 
									bridgeHandler.getName() 
							)
					)
					{
						return true;
					}
				}
			}
			return false;
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}

	public static final boolean isBridgeRemoveVisible( bridgeHandler bridgeHandler ) {
		try {
			if (netgest.bo.security.securityRights.canWrite( bridgeHandler.getEboContext() , bridgeHandler.getParent().getName() ) ) {
				boDefAttribute bridgeDef = bridgeHandler.getDefAttribute();
				if( bridgeDef.supportManualOperation() ) {
					if ( 
							netgest.bo.security.securityRights.canDelete( 
									bridgeHandler.getEboContext() ,
									bridgeHandler.getParent().getName(), 
									bridgeHandler.getName() 
							)
					)
					{
						return true;
					}
				}
			}
			return false;
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}
	
	public static final boolean canCreateNew( String objectName ) {
		try {
			if (netgest.bo.security.securityRights.canAdd( boApplication.currentContext().getEboContext(), objectName ) ) {
				return true;
			}
			return false;
		}
		catch ( boRuntimeException e ) {
			throw new RuntimeException( e );
		}
		
	}
	
}

