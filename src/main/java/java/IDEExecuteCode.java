public abstract class IDEExecuteCode {
    /**
     * Exécute le code Python donné en paramètre.
     * @param code Le code à exécuter
     */
    public abstract void executeCode(String code);
    public abstract void compileCode(String code);
}