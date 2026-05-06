package com.alejandro.lastmile.config;

import com.alejandro.lastmile.domain.Delivery;
import com.alejandro.lastmile.domain.Driver;
import com.alejandro.lastmile.domain.User;
import com.alejandro.lastmile.domain.enums.DeliveryPriority;
import com.alejandro.lastmile.domain.enums.DeliveryStatus;
import com.alejandro.lastmile.domain.enums.UserRole;
import com.alejandro.lastmile.repository.DeliveryRepository;
import com.alejandro.lastmile.repository.DriverRepository;
import com.alejandro.lastmile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final DeliveryRepository deliveryRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedEnabled;

    public DataSeeder(UserRepository userRepository,
                      DriverRepository driverRepository,
                      DeliveryRepository deliveryRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.seed.enabled:true}") boolean seedEnabled) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.deliveryRepository = deliveryRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedEnabled = seedEnabled;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }
        seedUsers();
        seedDrivers();
        seedDeliveries();
    }

    private void seedUsers() {
        if (!userRepository.existsByEmailIgnoreCase("admin@lastmile.test")) {
            userRepository.save(new User(
                    "Administrador LastMile",
                    "admin@lastmile.test",
                    passwordEncoder.encode("Admin123*"),
                    UserRole.ADMIN
            ));
        }
        if (!userRepository.existsByEmailIgnoreCase("alejandro@lastmile.test")) {
            userRepository.save(new User(
                    "Alejandro Ospina",
                    "alejandro@lastmile.test",
                    passwordEncoder.encode("Admin123*"),
                    UserRole.USER
            ));
        }
    }

    private void seedDrivers() {
        if (driverRepository.count() > 0) {
            return;
        }
        driverRepository.save(new Driver("Laura Restrepo", "+57 300 111 2233", "LMO-120", "Van electrica", 650.0, true));
        driverRepository.save(new Driver("Mateo Giraldo", "+57 301 222 3344", "LMO-245", "Moto carga", 120.0, true));
        driverRepository.save(new Driver("Camila Torres", "+57 302 333 4455", "LMO-318", "Camioneta", 900.0, true));
    }

    private void seedDeliveries() {
        if (deliveryRepository.count() > 0) {
            return;
        }
        deliveryRepository.save(new Delivery("Sofia Ramirez", "Carrera 43A #1-50, El Poblado", 6.2088, -75.5677, 8.5, DeliveryPriority.HIGH, DeliveryStatus.PENDING, "Entrega prioritaria oficina"));
        deliveryRepository.save(new Delivery("Andres Mejia", "Calle 10 #35-20, Manila", 6.2126, -75.5700, 3.2, DeliveryPriority.MEDIUM, DeliveryStatus.PENDING, "Paquete fragil"));
        deliveryRepository.save(new Delivery("Valentina Perez", "Circular 73B #39-60, Laureles", 6.2447, -75.5965, 5.0, DeliveryPriority.HIGH, DeliveryStatus.PENDING, "Llamar antes de llegar"));
        deliveryRepository.save(new Delivery("Daniel Gomez", "Carrera 70 #45E-31, Estadio", 6.2536, -75.5880, 12.0, DeliveryPriority.LOW, DeliveryStatus.PENDING, "Recepcion edificio"));
        deliveryRepository.save(new Delivery("Mariana Castro", "Calle 49 #50-21, Centro", 6.2476, -75.5658, 2.4, DeliveryPriority.MEDIUM, DeliveryStatus.PENDING, "Horario comercial"));
        deliveryRepository.save(new Delivery("Juan Pablo Rios", "Carrera 80 #30A-45, Belen", 6.2332, -75.6041, 7.7, DeliveryPriority.HIGH, DeliveryStatus.PENDING, "Entregar en porteria"));
        deliveryRepository.save(new Delivery("Isabella Vargas", "Calle 30 #65-90, Guayabal", 6.2167, -75.5903, 4.4, DeliveryPriority.LOW, DeliveryStatus.PENDING, "Zona industrial"));
        deliveryRepository.save(new Delivery("Sebastian Ochoa", "Carrera 52 #71-84, Aranjuez", 6.2785, -75.5627, 6.1, DeliveryPriority.MEDIUM, DeliveryStatus.PENDING, "Casa azul"));
        deliveryRepository.save(new Delivery("Natalia Moreno", "Calle 44 #79-12, La America", 6.2500, -75.6073, 9.6, DeliveryPriority.HIGH, DeliveryStatus.PENDING, "Paquete pesado"));
        deliveryRepository.save(new Delivery("Tomas Herrera", "Carrera 65 #48-60, Suramericana", 6.2570, -75.5818, 1.8, DeliveryPriority.LOW, DeliveryStatus.PENDING, "Sobre documentos"));
    }
}
