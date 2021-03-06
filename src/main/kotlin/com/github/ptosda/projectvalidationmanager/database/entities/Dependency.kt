package com.github.ptosda.projectvalidationmanager.database.entities

import java.io.Serializable
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "dependency")
data class Dependency ( // TODO In case a specific dependency has new vulnerabilities, add these to the corresponding dependency
        @EmbeddedId
        val pk: DependencyPk,

        @Column(columnDefinition = "TEXT")
        val description: String?,

        var vulnerabilitiesCount: Int?,

        @ManyToMany(cascade = [CascadeType.ALL])
        val dependencies: Set<Dependency>,

        @OneToMany(mappedBy = "pk.dependency")
        val license: List<DependencyLicense>,

        @OneToMany(mappedBy = "pk.dependency", cascade = [CascadeType.ALL])
        val vulnerabilities: MutableList<DependencyVulnerability>,

        val direct : Boolean
) : Serializable
{
        override fun equals(other: Any?): Boolean {
                var result = false
                if (other is Dependency) {
                        val otherObj = other as Dependency?
                        result = this.pk.id == otherObj!!.pk.id &&
                                this.pk.mainVersion == otherObj.pk.mainVersion &&
                                this.description == otherObj.description
                }
                return result
        }

        override fun hashCode(): Int {
                return this.pk.id.hashCode() * 41 +
                        this.pk.mainVersion.hashCode() * 17 +
                        (this.description?.hashCode()?.times(41) ?: 15)
        }

        override fun toString(): String {
                return "id:" + pk.id +
                        "; main_version:" + pk.mainVersion +
                        "; description:" + description +
                        "; vulnerabilitiesCount:" + vulnerabilitiesCount
        }

        @Transient
        var title = ""
        get() = pk.id.replace('/',':')
}