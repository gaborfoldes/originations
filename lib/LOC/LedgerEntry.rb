module LOC

	class LedgerEntry

		TOTAL = 0
		PRINCIPAL = 1
		INTEREST = 2
		FEE = 3

		CREDIT = true
		DEBIT = false

		HEADER = "PostDate\tType     \tDescription\tAmount\n--------\t----     \t-----------\t------"
		
		attr_accessor :type, :desc, :post_date, :amount, :credit
		
		def initialize(type, desc, post_date, amount, credit)
			@type = type
			@desc = desc
			@post_date = post_date
			@amount = amount
			@credit = credit ? 1 : -1
		end
		
		def type_str
			["Unknown ", "Principal", "Interest ", "Fee      "][@type]
		end
	
		def signed_amount
			@amount * @credit
		end
		
		def to_s
			[@post_date, type_str, @desc, sprintf("%0.3f", signed_amount)].join("\t")
		end
		
	end

end