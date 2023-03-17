function fetch_callback(response, invoice, invoice_type) {
  try {
    console.log(JSON.stringify(response))
    if (invoice_type === 'one_time') window.location.reload()
    else if (invoice_type === 'reusable') {
      const loc_without_https = document.location.href.replaceAll('https://', '').replaceAll('http://', '');
      var protocol = 'http://'
      if (document.location.href.includes('https://'))
        protocol = 'https://'

      const new_url = protocol + loc_without_https.substring(0, loc_without_https.indexOf('/') + 1) + invoice + '?session=' + response.data.sessionId
      document.location = new_url
    }
  }catch (e) {
    alert('Payment system selection error, please choose another payment method or try again later')
  }
}

function confirm(invoice, invoice_type, curr, psys) {
  try {
    console.log("Confirmation requested")

    if (curr !== undefined)
      switch (curr) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
          fetch('https://test.api.cashinout.io/invoices/createSession', {
            method: 'POST',
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({
              "invoiceId": invoice,
              "currency": curr
            })
          })
              .then(response => response.json())
              .then(response => {
                fetch_callback(response, invoice, invoice_type)
              })
          break
        case 5:
          if (psys !== undefined)
            fetch('https://test.api.cashinout.io/invoices/createSession', {
              method: 'POST',
              headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                "invoiceId": invoice,
                "currency": curr,
                "paymentSystem": psys
              })
            })
                .then(response => response.json())
                .then(response => {
                  fetch_callback(response, invoice, invoice_type)
                })

          break
      }
  }catch(e) {
    alert('Payment system selection error, please choose another payment method or try again later')
  }
}
