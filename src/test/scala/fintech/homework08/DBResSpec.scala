package fintech.homework08
import java.time.LocalDate

import org.scalatest.{FlatSpec, Matchers}
import fintech.homework08.PeopleApp.{Person, getOldPerson, clonePerson, storePerson, readPerson}

class DBResSpec extends FlatSpec with Matchers {
  val uri = "jdbc:h2:~/dbres"
  val p1 = Person("Alice", LocalDate.of(1970, 1, 1))
  val p2 = Person("Bob", LocalDate.of(1981, 5, 12))
  val p3 = Person("Charlie", LocalDate.of(1979, 2, 20))

  "DBRes" should "correct work with single execute for old task" in {
    val (clone, enteredPersons) = (for {
      _ <- DBRes.update("DROP TABLE IF EXISTS people", List.empty)
      _ <- DBRes.update("CREATE TABLE people(name VARCHAR(256), birthday DATE)", List.empty)

      _ <- storePerson(p1)
      _ <- storePerson(p2)
      _ <- storePerson(p3)
      old <- getOldPerson()
      resClone <- clonePerson(old)
      persons <- DBRes.select("SELECT * FROM people", List.empty)(readPerson)
    } yield (resClone, persons)).execute(uri)

    clone should be (p1.copy(birthday = LocalDate.now()))
    enteredPersons should be (List(p1, p2, p3, p1.copy(birthday = LocalDate.now())))
  }
}
