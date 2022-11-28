package it.linksmt.assatti.service.exception;

public class ExceptionUtil {
	public static Throwable getLastExceptionCause(Throwable e) {
		if(e!=null) {
			if(e.getCause()!=null && e.getCause()!=e) {
				return ExceptionUtil.getLastExceptionCause(e.getCause());
			}else if(e.getCause()!=null) {
				return e.getCause();
			}else {
				return e;
			}
		}else {
			return null;
		}
	}
}
