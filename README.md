# CSEDemo-shop

> A demo to show transaction function of CSE.

This demo is consist of account-service, goods-service, purchase-service.

The account-service records account info of customers, goods-service records
the quantity and unit price of goods, purchase-service records customers'
purchase log.

When customers buy goods, the balance of their accounts and the quantity
of the goods will be deducted, and purchase record will be added by
purchase-service.

If the balance of account and the quantity of goods is enough, the purchase
record will be completed. Otherwise, the status of the purchase record will
be cancelled.
