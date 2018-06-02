package com.github.ptosda.projectvalidationmanager.uiController

import com.github.ptosda.projectvalidationmanager.database.entities.*
import com.github.ptosda.projectvalidationmanager.database.repositories.*
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import kotlin.collections.set

@Controller
@RequestMapping("/")
class ReportController(val reportService: ReportService,
                       val buildRepo: BuildRepository,
                       val projectRepo: ProjectRepository,
                       val dependencyRepo: DependencyRepository,
                       val vulnerabilityRepo: VulnerabilityRepository,
                       val licenseRepo: LicenseRepository) {

    /**
     * Get the latest build reports received. Show projects and add a filter ( group, repository )
     */
    @GetMapping
    fun getHome(model: HashMap<String, Any>) : String{

        model["page_title"] = "Home"

        val projects = projectRepo.findAll()

        model["projects"] = projects

        return "home"
    }

    /**
     * Get the details of a dependency ( Dependencies, Licenses and Vulnerabilities )
     */
    @GetMapping("deps")
    fun getDependencies(model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Dependencies"

        model["dependencies"] = dependencyRepo.findAll()

        return "dependency-list"
    }

    /**
     * Get the details of a dependency ( Dependencies, Licenses and Vulnerabilities )
     */
    @GetMapping("licenses")
    fun getLicenses(model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Licenses"

        model["licenses"] = licenseRepo.findAll()

        return "license-list"
    }

    /**
     * Tem que se verificar a chave primaria de projecto pois pode haver com nomes iguais
     */
    @GetMapping("projs/{project-name}")
    fun getProjectDetail(@PathVariable("project-name") projectName: String,
                         model: HashMap<String, Any>) : String{

        model["page_title"] = "Project Builds"

        val builds = projectRepo.findById(projectName).get().build!!

        model["project_id"] = projectName
        model["builds"] = builds

        val licenses = ArrayList<Any>()
        val vulnerabilities = ArrayList<Any>()

        builds.forEach{
            licenses.addAll(reportService.getBuildLicenses(it))
            vulnerabilities.addAll(reportService.getDependencyVulnerabilities(it.dependency!!.toList()))
        }

        return "project"
    }

    @GetMapping("projs/{project-id}/report/{build-id}")
    fun getBuild(@PathVariable("project-id") projectId: String,
                 @PathVariable("build-id") buildId: String,
                 model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Report Detail"

        val buildInfo = buildRepo.findById(BuildPk(buildId, Project(projectId, null, null)))

        if(!buildInfo.isPresent) {
            throw Exception("Build was not found")
        }

        val build = buildInfo.get()

        model["project_id"] = projectId

        model["build_id"] = buildId
        model["build_tag"] = build.tag

        model.putAll(reportService.getBuildDependencies(build))

        return "build"
    }

    /**
     * Get the details of a dependency ( Dependencies, Licenses and Vulnerabilities )
     */
    @GetMapping("projs/{project-id}/report/{build-id}/deps/{dependency-id}/version/{dependency-version}")
    fun getDependencyDetail(@PathVariable("project-id") projectId: String,
                            @PathVariable("build-id") buildId: String,
                            @PathVariable("dependency-id") dependencyId: String,
                            @PathVariable("dependency-version") version: String,
                            model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Dependency Detail"

        val dependencyInfo = dependencyRepo.findById(DependencyPk(dependencyId, Build(BuildPk(buildId, Project(projectId, null, null)), null, null), version))

        if(!dependencyInfo.isPresent) {
            throw Exception("Dependency not found")
        }

        val dependency = dependencyInfo.get()

        model["project_id"] = projectId
        model["build_id"] = buildId

        model["title"] = dependency.pk.id
        model["main_version"] = dependency.pk.mainVersion
        model["description"] = dependency.description
        model["license"] = dependency.license
        model["vulnerabilities"] = dependency.vulnerabilities

        return "dependency-detail"
    }

    /**
     * Get the details of a dependency ( Dependencies, Licenses and Vulnerabilities )
     */
    @GetMapping("licenses/{license-id}")
    fun getLicense(@PathVariable("license-id") licenseId: String,
                            model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Dependency Detail"

        val licenseInfo = licenseRepo.findById(licenseId)

        if(!licenseInfo.isPresent) {
            throw Exception("License not found")
        }

        val license = licenseInfo.get()

        model["license_id"] = license.spdxId
        model["dependencies"] = license.dependencies
        model["error_info"] = license.errorInfo

        return "license-detail"
    }

    /**
     * Get the details of a dependency ( Dependencies, Licenses and Vulnerabilities )
     */
    @GetMapping("deps/{dependency-id}/vulnerability/{vulnerability-id}")
    fun getVulnerability(@PathVariable("dependency-id") dependencyId: String,
                         @PathVariable("vulnerability-id") vulnerabilityId: Long,
                   model: HashMap<String, Any?>) : String
    {
        model["page_title"] = "Vulnerability Detail"

        val vulnerabilityInfo = vulnerabilityRepo.findById(vulnerabilityId)

        if(!vulnerabilityInfo.isPresent ) {
            throw Exception("License not found")
        }
        val vulnerability = vulnerabilityInfo.get()

        model["dependency_id"] = dependencyId

        model["vulnerability"] = vulnerability

        return "vulnerability-detail"
    }

}