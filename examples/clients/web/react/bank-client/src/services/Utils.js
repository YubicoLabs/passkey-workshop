const Utils = {
  timeoutUtil,
  convertDate
}

async function timeoutUtil(timeout) {
  await new Promise((resolve) => {
    setTimeout(() => resolve(), timeout);
  })
}

function convertDate(myDate) {
  return new Date(myDate).toLocaleString();
}

export default Utils;