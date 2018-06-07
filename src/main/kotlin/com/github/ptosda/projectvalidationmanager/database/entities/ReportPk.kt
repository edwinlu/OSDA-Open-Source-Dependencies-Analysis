package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Embeddable
data class ReportPk(
        val timestamp: String,  // TODO Edit to a human readable DateTime

        @ManyToOne
        @JoinColumn(name = "name")
        val project: Project
) : Serializable