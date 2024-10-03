package org.example.warehouse;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WarehouseRepository implements PanacheRepository<WarehouseEntity> {
}
