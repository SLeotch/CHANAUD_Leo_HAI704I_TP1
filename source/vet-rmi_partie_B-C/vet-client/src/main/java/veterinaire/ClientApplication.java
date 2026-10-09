package veterinaire;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        try{
            for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if("Nimbus".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored){}
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    public ICabinetVeterinaire cabinet() throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        return (ICabinetVeterinaire) registry.lookup("cabinet");
    }

    @Bean
    public ClientLogic clientLogic(ICabinetVeterinaire cabinet) {
        return new ClientLogic(cabinet, "Vétérinaire");
    }

    @Bean
    public ApplicationRunner lancerClient(ClientLogic logique) {
        return args -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                VeterinaireGUI gui = new VeterinaireGUI(logique);
                gui.setVisible(true);
            });
        };
    }
}