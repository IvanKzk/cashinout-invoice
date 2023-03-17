function check_pay(payment_id) {
  fetch('https://test.api.cashinout.io/invoices/invoice/'+payment_id, {
    method: 'GET'
  })
      .then(response => response.json())
      .then(response => {
        console.log(JSON.stringify(response))
        if (response.data.status !== "open") {
            const status_el = document.querySelector('.gr-status')
            status_el.innerHTML = response.data.status
            status_el.classList.toggle("processing")
        }
        if (response.data.session == undefined) {
            const loc_without_https = document.location.href.replaceAll('https://', '').replaceAll('http://', '');
            var protocol = 'http://'
            if (document.location.href.includes('https://'))
                protocol = 'https://'
            document.location = protocol + loc_without_https.substring(0, loc_without_https.indexOf('/')+1)+payment_id
        }
      })
}

function startChecking(payment_id) {
  setInterval(check_pay, 30000, payment_id)
}