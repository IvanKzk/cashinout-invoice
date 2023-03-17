package ru.cashinout.ru.cashinout

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.cashinout.Const.api_info_url
import ru.cashinout.Const.api_sessions_url
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


@Controller
class WebPublicController {

    private val log: Logger = LoggerFactory.getLogger("Web")

    private fun getSelectedCurLabel(selected_cur: Int, paymentSystem: String? = null): String? {
        var selected_cur_label: String? = null
        when (selected_cur) {
            0 -> selected_cur_label = "RUB"
            1 -> selected_cur_label = "UAH"
            2 -> selected_cur_label = "KZT"
            3 -> selected_cur_label = "BTC"
            4 -> selected_cur_label = "LTC"
            5 -> {
                when (paymentSystem) {
                    "trc20" -> selected_cur_label = "USDT (trc20)"
                    "erc20" -> selected_cur_label = "USDT (erc20)"
                    "bep20" -> selected_cur_label = "USDT (bep20)"
                }
            }
            6 -> selected_cur_label = "EUR"
            7 -> selected_cur_label = "USD"
            8 -> selected_cur_label = "BNB"
            9 -> selected_cur_label = "ETH"
            10 -> selected_cur_label = "TRX"
        }
        return selected_cur_label
    }

    private fun getSelectedCurLabel(selected_cur: String, paymentSystem: String? = null): String? {
        var selected_cur_label: String? = null
        when (selected_cur) {
            "rub" -> selected_cur_label = "RUB"
            "uah" -> selected_cur_label = "UAH"
            "kzt" -> selected_cur_label = "KZT"
            "btc" -> selected_cur_label = "BTC"
            "ltc" -> selected_cur_label = "LTC"
            "trc20_usdt" -> selected_cur_label = "USDT (trc20)"
            "erc20_usdt" -> selected_cur_label = "USDT (erc20)"
            "bep20_usdt" -> selected_cur_label = "USDT (bep20)"
            "eur" -> selected_cur_label = "EUR"
            "usd" -> selected_cur_label = "USD"
            "bnb" -> selected_cur_label = "BNB"
            "eth" -> selected_cur_label = "ETH"
            "trx" -> selected_cur_label = "TRX"
        }
        return selected_cur_label
    }

    @RequestMapping("/{id}")
    fun main(
        @PathVariable(required = true) id: String,
        @RequestParam(required = false) selected_cur: Int? = null,
        @RequestParam(required = false) paymentSystem: String? = null,
        @RequestParam(required = false) confirmed: Boolean? = null,
        @RequestParam(name = "session",required = false) cookie_session: String? = null,
        model: Model
    ): String {
        try {
            model.addAttribute("error", false)
            //val values = mapOf("name" to "John Doe", "occupation" to "gardener")

            val objectMapper = ObjectMapper()
            val requestBody: String = ""//objectMapper.writeValueAsString(values)

            val client = HttpClient.newBuilder().build();
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$api_info_url$id"))
                //.POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            println(response.body())
            val responseJSON = JSONObject(response.body())
            val data_json = JSONObject(responseJSON["data"].toString())


            model.addAttribute("recipient", "cashinout.io")
            model.addAttribute("recipient_link", "\"https://cashinout.io/\"")

            model.addAttribute("order_id", id)
            model.addAttribute("invoice_type", data_json["type"])
            if (data_json["type"] == "one_time") model.addAttribute("amount", data_json["amount"])
            model.addAttribute("already_paid", data_json["currentAmountUsdt"])
            model.addAttribute("payment_status", data_json["status"])

            val session: JSONObject? =
                if (data_json["type"] == "one_time") {
                    if (JSONObject.NULL.equals(data_json["session"])) null
                    else JSONObject(data_json["session"].toString())
                } else
                    if ((data_json["type"] == "reusable") && (cookie_session != null)) {
                        val request = HttpRequest.newBuilder()
                            .uri(URI.create("$api_sessions_url$cookie_session"))
                            .build()
                        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
                        JSONObject(JSONObject(response.body())["data"].toString())
                    }
                    else null

            //model.addAttribute("selected_curr_amount", "10000") //TODO !!
            if(session == null) {
                if (selected_cur == null) {
                    return "main"
                } else {
                    val selected_cur_label = getSelectedCurLabel(selected_cur, paymentSystem)
                    if (selected_cur_label != null) {
                        model.addAttribute("selected_curr", selected_cur_label)
                        return "confirm"

                    } else return "main"
                }
            } else {
                if (!JSONObject.NULL.equals(session["limitAmount"])) model.addAttribute("selected_curr_amount", session["limitAmount"])
                model.addAttribute("selected_curr", getSelectedCurLabel(session.getString("currency"), paymentSystem)) //TODO paymentSystem
                model.addAttribute("session_id", session.getString("sessionId"))

                val requisites = JSONObject(session["requisites"].toString())
                model.addAttribute("requisites_type", requisites.getString("type"))
                if (requisites["type"] == "crypto")
                    model.addAttribute("selected_curr_address", requisites["address"])
                else if (requisites["type"] == "bank_card") {
                    val card = JSONObject(requisites["card"].toString())
                    model.addAttribute("selected_curr_address", card["cardNumber"])
                    model.addAttribute("fullName", card["fullName"])
                }
                return "payment"
            }
        }catch (e: Exception) {
            e.printStackTrace()
            model.addAttribute("order_id", "ERROR")
            model.addAttribute("amount", "ERROR")
            model.addAttribute("error", true)
            return "main"
        }
    }
}