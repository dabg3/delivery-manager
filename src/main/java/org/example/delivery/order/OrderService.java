package org.example.delivery.order;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.delivery.location.GeoPoint;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
class OrderService {

    private final OrderRepository orderRepository;

    @Inject
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<ShoppingOrderEntity> retrieveOrders(String sortField,
                                                    String order,
                                                    Optional<String> stateFilter) {
        Sort.Direction d = switch (order.toLowerCase()) {
            case "descending", "desc" -> Sort.Direction.Descending;
            default -> Sort.Direction.Ascending;
        };
        Sort s = Sort.by(sortField, d);
        PanacheQuery<ShoppingOrderEntity> query;
        Optional<ShoppingOrderEntity.State> state = parseStateFilter(stateFilter);
        if (state.isPresent()) {
            query = orderRepository.find(
                    "select o from ShoppingOrder o where o.state = ?1",
                    s,
                    state.get()
            );
        } else {
            query = orderRepository.findAll(s);
        }
        return query.list();
    }

    private Optional<ShoppingOrderEntity.State> parseStateFilter(Optional<String> stateFilter) {
        if (stateFilter.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(ShoppingOrderEntity.State.valueOf(stateFilter.get().toUpperCase()));
        } catch (IllegalArgumentException _) {
            return Optional.empty();
        }
    }

    @Transactional
    public void updateAddress(Long id, GeoPoint address) {
        ShoppingOrderEntity order = orderRepository.findById(id);
        order.setDeliveryAddress(address);
        orderRepository.flush();
    }
}
