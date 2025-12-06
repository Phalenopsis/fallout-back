package eu.nicosworld.falloutback.exception;

public class EmailAlreadyUsed extends Exception{
    @Override
    public String getMessage() {
        return "Cet email est déjà utilisé.";
    }
}
