// vet-server/src/main/java/veterinaire/RmiServerConfig.java
package veterinaire;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Configuration
public class RmiServerConfig {

    @Value("${vet.rmi.port}")
    private int port;

    @Value("${vet.rmi.service-name}")
    private String serviceName;

    @Bean(destroyMethod = "") // Registry n'a pas de close()
    public Registry rmiRegistry() throws Exception {
        return LocateRegistry.createRegistry(port);
    }

    @Bean
    public ApplicationRunner publierCabinet(Registry rmiRegistry, ICabinetVeterinaire cabinet) {
        return args -> {
            rmiRegistry.rebind(serviceName, cabinet);
            System.out.println("Cabinet publié sous '" + serviceName + "' sur le port " + port);
        };
    }

    @Bean
    public AutoCloseable rmiShutdown(Registry rmiRegistry, ICabinetVeterinaire cabinet) {
        return () -> {
            rmiRegistry.unbind(serviceName);
            UnicastRemoteObject.unexportObject(cabinet, true);
        };
    }
}