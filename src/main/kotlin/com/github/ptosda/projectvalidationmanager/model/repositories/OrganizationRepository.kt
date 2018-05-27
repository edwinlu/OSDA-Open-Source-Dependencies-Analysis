package com.github.ptosda.projectvalidationmanager.model.repositories

import com.github.ptosda.projectvalidationmanager.model.entities.Organization
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationRepository : CrudRepository<Organization, String>