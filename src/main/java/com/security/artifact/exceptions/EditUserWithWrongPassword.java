package com.security.artifact.exceptions;

public class EditUserWithWrongPassword extends RuntimeException{
	
	public EditUserWithWrongPassword() {
		super("Las contraseñas es incorrecta");
	}
}
