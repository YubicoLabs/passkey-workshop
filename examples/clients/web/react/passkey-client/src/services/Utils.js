const Utils = {
  timeoutUtil
}

async function timeoutUtil(timeout) {
  await new Promise((resolve) => {
    setTimeout(() => resolve(), timeout);
  })
}

export default Utils;