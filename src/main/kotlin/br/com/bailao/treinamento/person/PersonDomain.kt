package br.com.bailao.treinamento.person

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "person")
class Person {

    private constructor()

    constructor(personDTO: PersonDTO) : this() {
        this.id = UUID.randomUUID().toString()
        edit(personDTO)
    }

    @Id
    @Column(name = "id")
    lateinit var id: String
        private set

    @Column(name = "first_name")
    lateinit var firstName: String
        private set

    @Column(name = "last_name")
    lateinit var lastName: String
        private set

    @Column(name = "state")
    lateinit var state: String
        private set

    companion object {
        fun nullObjectPerson(): Person {
            return Person()
        }
    }

    fun edit(personDTO: PersonDTO) {
        lastName = personDTO.lastName
        firstName = personDTO.firstName
        state = personDTO.state
    }

}

class PersonDTO {

    @JsonProperty("firstName")
    lateinit var firstName: String

    @JsonProperty("lastName")
    lateinit var lastName: String

    @JsonProperty("state")
    var state: String = ""
}

@RestController
@RequestMapping("/api/v1/persons")
class PersonController(val personService: PersonService) {

    @PostMapping
    fun create(@RequestBody personDTO: PersonDTO): Person = personService.save(personDTO)

    @PutMapping(params = ["personId"])
    fun put(@RequestParam("personId") personId: String, @RequestBody personDTO: PersonDTO): Person = personService.edit(personId, personDTO)

    @GetMapping
    fun get(pageable: Pageable): MutableIterable<Person> = personService.findAll(pageable)

    @GetMapping(params = ["personId"])
    fun getById(@RequestParam("personId") personId: String): Person? = personService.findById(personId)

    @DeleteMapping(params = ["personId"])
    fun delete(@RequestParam("personId") personId: String) = personService.delete(personId)

}

@Service
class PersonService(val personRepository: PersonRestRepository) {
    fun save(personDTO: PersonDTO): Person = personRepository.save(Person(personDTO))

    fun findAll(pageable: Pageable): MutableIterable<Person> = personRepository.findAll(pageable)

    fun findById(personId: String): Person? = personRepository.findById(personId).orElseGet { null }

    fun edit(personId: String, personDTO: PersonDTO): Person {
        val person = findById(personId)!!
        person.edit(personDTO)
        return save(personDTO)
    }

    fun delete(personId: String) = personRepository.delete(findById(personId)!!)

}

@Repository
interface PersonRestRepository : PagingAndSortingRepository<Person, String>
