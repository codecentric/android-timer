package de.codecentric.android.timer;

import java.lang.reflect.Method;

import org.mockito.internal.util.reflection.Whitebox;

/**
 * Extension of Mockito's Whitebox class that also supports calling private
 * methods.
 * 
 * @author Bastian Krol
 */
public class TimerWhitebox extends Whitebox {

	/**
	 * Calls the method named {@code methodName} on the object {@code target}
	 * with the given {@code parameters} by reflection even if it is
	 * unaccessible to the caller. The parameter types (which are needed to find
	 * the correct method) are inferred from {@code parameters}. This inferring
	 * will not work if one of the parameters is null or the method has
	 * primitive parameters. For these cases,
	 * {@link #callInternalMethod(Object, String, Object[], Class[])}.
	 * 
	 * @param target
	 *            the object on which the method is invoked
	 * @param methodName
	 *            the name of the method
	 * @param parameters
	 *            the parameters to pass to the method, can be omitted if the
	 *            method has no parameters
	 */
	public static void callInternalMethod(Object target, String methodName,
			Object... parameters) {
		Class<?>[] parameterTypes = createParameterTypeArrayOnTheFly(parameters);
		callInternalMethod(target, methodName, parameters, parameterTypes);
	}

	/**
	 * Calls the method named {@code methodName} which's signature is determined
	 * by {@code parameterTypes} on the object {@code target} with the given
	 * {@code parameters} by reflection even if it is unaccessible to the
	 * caller. This method variant is useful if the parameter types can not be
	 * inferred correctly from the passed parameters, otherwise also
	 * {@link #callInternalMethod(Object, String, Object...)} can be used.
	 * 
	 * @param target
	 *            the object on which the method is invoked
	 * @param methodName
	 *            the name of the method
	 * @param parameters
	 *            the parameters to pass to the method, can be omitted if the
	 *            method has no parameters
	 * @param parameterTypes
	 *            the types of the parameters
	 */
	public static void callInternalMethod(Object target, String methodName,
			Object[] parameters, Class<?>[] parameterTypes) {
		Class<?> clazz = target.getClass();
		try {
			Method method = getMethodFromHierarchy(clazz, methodName,
					parameterTypes);
			method.setAccessible(true);
			method.invoke(target, parameters);
		} catch (Exception e) {
			throw new RuntimeException("Unable to call internal method.", e);
		}
	}

	private static Method getMethodFromHierarchy(Class<?> clazz,
			String methodName, Class<?>[] parameterTypes) {
		Method method = getMethodFromHierarchyRecursive(clazz, methodName,
				parameterTypes);
		if (method == null) {
			StringBuilder message = new StringBuilder();
			message.append("You want me to call the method: '");
			message.append(methodName);
			message.append("(");
			for (int i = 0; i < parameterTypes.length; i++) {
				Class<?> parameter = parameterTypes[i];
				message.append(parameter.getSimpleName());
				if (i < parameterTypes.length - 1) {
					message.append(", ");
				}
			}
			message.append(")");
			message.append("' on the class: '");
			message.append(clazz.getSimpleName());
			message.append("' but this method is not declared within hierarchy of this class.");
			throw new RuntimeException(message.toString());
		}
		return method;
	}

	private static Method getMethodFromHierarchyRecursive(Class<?> clazz,
			String methodName, Class<?>[] parameterTypes) {
		Method method = getMethod(clazz, methodName, parameterTypes);
		while (method == null && clazz != Object.class) {
			clazz = clazz.getSuperclass();
			method = getMethodFromHierarchyRecursive(clazz, methodName,
					parameterTypes);
		}
		return method;
	}

	private static Method getMethod(Class<?> clazz, String method,
			Class<?>[] parameterTypes) {
		try {
			return clazz.getDeclaredMethod(method, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	private static Class<?>[] createParameterTypeArrayOnTheFly(
			Object[] parameters) {
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterTypes[i] = parameters[i].getClass();
		}
		return parameterTypes;
	}
}
