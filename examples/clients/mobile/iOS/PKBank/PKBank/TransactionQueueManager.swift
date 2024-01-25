//
//  TransactionQueueManager.swift
//  PKBank
//
//  Created by Dennis Hills on 1/24/24.
//
import Foundation

class TransactionQueueManager {
    
    static let shared = TransactionQueueManager()
    private let transactionsKey = "transactionsQueue"
    
    private init() {}
    
    func storeTransactions(_ transactions: [Transaction]) {
        let defaults = UserDefaults.standard
        let encoded = encodeTransactions(transactions)
        defaults.set(encoded, forKey: transactionsKey)
    }
    
    func retrieveTransactions() -> [Transaction] {
        let defaults = UserDefaults.standard
        guard let data = defaults.data(forKey: transactionsKey),
              let transactions = decodeTransactions(from: data) else {
            return []
        }
        return transactions
    }
    
    func addTransaction(_ transaction: Transaction) {
        var transactions = retrieveTransactions()
        transactions.append(transaction)
        storeTransactions(transactions)
    }
    
    func removeTransaction(at index: Int) {
        var transactions = retrieveTransactions()
        guard transactions.indices.contains(index) else { return }
        transactions.remove(at: index)
        storeTransactions(transactions)
    }
    
    func clearTransactions() {
        let defaults = UserDefaults.standard
        defaults.set(nil, forKey: transactionsKey)
    }
    
    func encodeTransactions(_ transactions: [Transaction]) -> Data? {
        return try? JSONEncoder().encode(transactions)
    }

    func decodeTransactions(from data: Data) -> [Transaction]? {
        return try? JSONDecoder().decode([Transaction].self, from: data)
    }
}

struct Transaction: Codable {
    var transactionType: BankTransactionType
    var amount: Double
    var desc: String
}
