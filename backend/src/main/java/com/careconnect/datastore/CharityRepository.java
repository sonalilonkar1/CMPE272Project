package com.careconnect.datastore;

import com.careconnect.model.Charity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharityRepository extends JpaRepository<Charity, Long> {

}
