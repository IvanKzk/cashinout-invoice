function cancelPayment(invoice_id, payment_id) {
  fetch('https://test.api.cashinout.io/invoices/closeSession/'+payment_id, {
    method: 'POST'
  }).then(response => response.json())
      .then(response => {
        console.log(JSON.stringify(response))
        const loc_without_https = document.location.href.replaceAll('https://', '').replaceAll('http://', '');
        var protocol = 'http://'
        if (document.location.href.includes('https://'))
          protocol = 'https://'
        document.location = protocol + loc_without_https.substring(0, loc_without_https.indexOf('/')+1)+invoice_id
      })
}

function copyToClipboard(textToCopy) {
  // navigator clipboard api needs a secure context (https)
  if (navigator.clipboard && window.isSecureContext) {
    // navigator clipboard api method'
    return navigator.clipboard.writeText(textToCopy);
  } else {
    // text area method
    let textArea = document.createElement("textarea");
    textArea.value = textToCopy;
    // make the textarea out of viewport
    textArea.style.position = "fixed";
    textArea.style.left = "-999999px";
    textArea.style.top = "-999999px";
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    return new Promise((res, rej) => {
      // here the magic happens
      document.execCommand('copy') ? res() : rej();
      textArea.remove();
    });
  }
}

function copy(selector) {
  const copyText = document.querySelector(selector);
  try{
    copyText.focus();
    copyText.select();
    copyText.setSelectionRange(0, 99999);
  }catch (e) {}

  let copyValue = copyText.value;
  if (copyValue === undefined)
    copyValue = copyText.innerHTML
  if (copyValue.includes(' '))
    copyValue = copyValue.substring(0, copyValue.indexOf(' '))
  copyToClipboard(copyValue)
}
