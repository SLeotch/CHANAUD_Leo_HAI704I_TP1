package veterinaire;

/**
 * Exception NON vérifiée, purement locale au client. ClientLogic attrape les
 * RemoteException issues de java.rmi et les traduit vers celle-ci : c'est ce
 * qui permet à ConsoleInterface de ne jamais avoir besoin d'importer
 * java.rmi (contrainte de l'étape A8).
 */
public class ErreurServiceDistant extends RuntimeException {

    public ErreurServiceDistant(String message, Throwable cause) {
        super(message, cause);
    }

    public ErreurServiceDistant(String message) {
        super(message);
    }
}
