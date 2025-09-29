package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.payback.ReturnItemResponseDTO;
import com.lcdev.ecommerce.application.dto.payback.ReturnResponseDTO;
import com.lcdev.ecommerce.domain.entities.ReturnItem;
import com.lcdev.ecommerce.domain.entities.ReturnRequest;

public interface ReturnMapper {
    ReturnResponseDTO toResponse(ReturnRequest entity);
    ReturnItemResponseDTO toResponse(ReturnItem entity);
}
