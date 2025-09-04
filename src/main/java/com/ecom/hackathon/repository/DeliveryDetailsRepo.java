package com.ecom.hackathon.repository;

import com.ecom.hackathon.dto.DeliveryDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryDetailsRepo extends MongoRepository<DeliveryDetails, String> {
}
