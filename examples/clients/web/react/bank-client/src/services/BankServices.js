const BankServices = {
  getAdvancedProtectionStatus,
  setAdvancedProtection,
  getAccounts,
  getTransactions,
  createTransactions,
  getAccount
};

const baseURL = process.env.REACT_APP_API || "http://localhost:8082/v1";

async function getAdvancedProtectionStatus(accountId) {
  /**
   * PREDEV Mock, remove the return statement when API is live
   */

  return {
    accountId: 1349,
    enabled: false,
  };
  try {
    const requestOptions = {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    };

    const response = await fetch(
      `${baseURL}/account/${accountId}/advanced-protection`,
      requestOptions
    );
    const responseJSON = await response.json();

    console.info(`Printing response for ${accountId}`);
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("API call failed. See the message below for details");
    console.error(e.message);
    throw e;
  }
}

async function setAdvancedProtection(accountId, newSetting) {
  /**
   * PREDEV Mock, remove the return statement when API is live
   */

  return {
    accountId: 1349,
    enabled: newSetting,
  };
  try {
    const reqData = {
      enabled: newSetting,
    };

    const requestOptions = {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(reqData),
    };

    const response = await fetch(
      `${baseURL}/account/${accountId}/advanced-protection`,
      requestOptions
    );
    const responseJSON = await response.json();

    console.info(`Printing response for ${accountId}`);
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("API call failed. See the message below for details");
    console.error(e.message);
    throw e;
  }
}

async function getAccounts() {
  try {
    const requestOptions = {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    };

    const response = await fetch(`${baseURL}/accounts`, requestOptions);
    const responseJSON = await response.json();

    console.info(`Printing response for this user's account`);
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("API call failed. See the message below for details");
    console.error(e.message);
    throw e;
  }
}

async function getTransactions(accountId) {
  try {
    const requestOptions = {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    };

    const response = await fetch(
      `${baseURL}/account/${accountId}/transactions`,
      requestOptions
    );
    const responseJSON = await response.json();

    console.info(`Printing transactions for ${accountId}`);
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("API call failed. See the message below for details");
    console.error(e.message);
    throw e;
  }
}

async function createTransactions(accountId, type, amount, description) {
  /**
   * PREDEV Mock, remove the return statement when API is live
   */
  try {
    const reqData = {
      type: type,
      amount: amount,
      description: description,
    };

    const requestOptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(reqData),
    };

    const response = await fetch(
      `${baseURL}/account/${accountId}/transactions`,
      requestOptions
    );
    const responseJSON = await response.json();

    console.info(`Printing transactions for ${accountId}`);
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("API call failed. See the message below for details");
    console.error(e.message);
    throw e;
  }
}

async function getAccount(accountId) {
  return {
    accountId: 1234,
    balance: 100000,
    advancedProtection: false,
  };
  try {
    const requestOptions = {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    };

    const response = await fetch(
      `${baseURL}/account/${accountId}`,
      requestOptions
    );
    const responseJSON = await response.json();

    console.info(`Printing response for this user's account`);
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("API call failed. See the message below for details");
    console.error(e.message);
    throw e;
  }
}

export default BankServices;