package ru.cashinout

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime


@SpringBootTest
class DemoApplicationTests {

	@Test
	fun contextLoads() {
	}

	@Test
	@Throws(JsonProcessingException::class)
	fun whenSerializingJava8Date_thenCorrect() {
		val date = LocalDateTime.of(2014, 12, 20, 2, 30)
		val mapper = ObjectMapper()
		mapper.registerModule(JavaTimeModule())
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		val result = mapper.writeValueAsString(date)
		assertThat(result, containsString("2014-12-20T02:30"))
	}


	@Test
	@Throws(JsonProcessingException::class)
	fun whenSerializingJava8Date_thenCorrect2() {
		val lt = LocalDateTime.of(2014, 12, 20, 2, 30)
		val date = ZonedDateTime.of(lt, ZoneId.systemDefault())
		val mapper = ObjectMapper()
		mapper.registerModule(JavaTimeModule())
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		val result = mapper.writeValueAsString(date)
		println(result)
		assertThat(result, containsString("2014-12-20T02:30:00+03:00"))
	}

}
