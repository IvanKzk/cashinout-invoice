function confirm(invoice) {
  console.log("Confirmation requested")
  const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
  });

  let curr = parseInt(params.selected_cur)
  let psys = params.paymentSystem

  if (curr !== undefined)
    switch (curr) {
      case 3:
      case 4:
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
              console.log(JSON.stringify(response))
              //window.location.reload()
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
                console.log(JSON.stringify(response))
                //window.location.reload()
              })

        break
    }
}
