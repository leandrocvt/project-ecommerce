package com.lcdev.ecommerce.infrastructure.projections;

public interface UserMinProjection {
    Long getId();
    String getFirstName();
    String getLastName();
    String getEmail();
}
