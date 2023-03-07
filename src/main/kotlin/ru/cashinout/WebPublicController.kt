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
            3 -> selected_cur_label = "BTC"
            4 -> selected_cur_label = "LTC"
            5 -> {
                when (paymentSystem) {
                    "trc20" -> selected_cur_label = "USDT (trc20)"
                    "erc20" -> selected_cur_label = "USDT (erc20)"
                    "bep20" -> selected_cur_label = "USDT (bep20)"
                }
            }
            8 -> selected_cur_label = "BNB"
            9 -> selected_cur_label = "ETH"
            10 -> selected_cur_label = "TRX"
        }
        return selected_cur_label
    }

    @RequestMapping("/{id}")
    fun main(
        @PathVariable(required = true) id: String,
        @RequestParam(required = false) selected_cur: Int? = null,
        @RequestParam(required = false) paymentSystem: String? = null,
        @RequestParam(required = false) confirmed: Boolean? = null,
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
            if (data_json["type"] == "one_time") model.addAttribute("amount", data_json["amount"])
            model.addAttribute("already_paid", data_json["currentAmountUsdt"])

            val session: JSONObject? = if (JSONObject.NULL.equals(data_json["session"])) null else JSONObject(data_json["session"].toString())

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
                model.addAttribute("selected_curr_amount", session["limitAmount"])
                model.addAttribute("selected_curr", getSelectedCurLabel(session.getInt("currency"), paymentSystem)) //TODO paymentSystem

                val requisites = JSONObject(session["requisites"].toString())
                model.addAttribute("selected_curr_address", requisites["address"])
                model.addAttribute("payment_status", data_json["status"])
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