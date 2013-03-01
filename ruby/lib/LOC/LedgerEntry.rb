module LOC

	class LedgerEntry

		Total = 0
		Principal = 1
		Interest = 2
		Fee = 3

		Credit = true
		Debit = false

		Header = "PostDate\tType     \tDescription\tAmount\n--------\t----     \t-----------\t------"
		
		attr_accessor :type
		attr_accessor :desc
		attr_accessor :post_date
		attr_accessor :amount
		attr_accessor :credit
		
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
			[@post_date, type_str, @desc, sprintf("%0.2f", signed_amount)].join("\t")
		end
		
	end

end