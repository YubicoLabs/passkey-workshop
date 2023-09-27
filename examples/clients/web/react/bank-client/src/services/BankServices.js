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
  return {
    accounts: [
      {
        accountId: 1234,
        balance: 100000,
        advancedProtection: false,
      },
    ],
  };
  try {
    const requestOptions = {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    };

    const response = await fetch(`${baseURL}/account`, requestOptions);
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
  /**
   * PREDEV Mock, remove the return statement when API is live
   */

  return {
    transactions: [
      {
        transactionId: 1,
        type: "transfer",
        amount: 100,
        transactionDate: "AAAAAAAAA",
        description: "Best friend",
      },
      {
        transactionId: 1,
        type: "deposit",
        amount: 300,
        transactionDate: "BBBBBBBB",
        description: "Mom",
      },
      {
        transactionId: 1,
        type: "transfer",
        amount: 100,
        transactionDate: "CCCCCCC",
        description: "Someone",
      },
    ],
  };
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

  return {
    transactionId: 1,
    type: "transfer",
    amount: 100,
    transactionDate: "AAAAAAAAA",
    description: "Best friend",
  };
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