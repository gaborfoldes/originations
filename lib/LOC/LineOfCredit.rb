
module LOC

	class LineOfCredit 
	
		MONTHLY_FEE = 6.25
		INTEREST_RATE = 0.35
		DRAW_FEE_PCT = 0.03
		DRAW_FEE_MIN = 10.00
		DUE_AFTER = 14

		COL_WIDTHS = [11, 5, 11, 30, 11, 7, 7, 7, 7, 7, 11, 11, 7, 7, 11, 7]
		COL_JUST   = [1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0]
		COL_NAMES  = ["AsOfDate", "ID", "AppNumber", "Email", "Opened", "Line", "Pr.", "Fees", "Int.", "Balance", "PrevDue", "NextDue", "MinPay", "AmtDue", "LastPaid", "PaidAmt"]
		HEADER = COL_NAMES.each_with_index.map { |s, i|
			(COL_JUST[i] == 1) ? s.to_s[0..COL_WIDTHS[i]-1].ljust(COL_WIDTHS[i]) : s.to_s[0..COL_WIDTHS[i]-1].rjust(COL_WIDTHS[i])
		}.join(" ")
		
		attr_accessor :id, :app_number, :user_id, :email, :credit_line, :open_date
		attr_accessor :ledger, :payments
		attr_accessor :last_billing_date, :last_interest_date
		
		def initialize(open_date, id, app_number, user_id, email, credit_line, first_due_date) 
			@ledger = Ledger.new
			@payments = {}
			@id = id
			@app_number = app_number
			@user_id = user_id
			@email = email
			@open_date = open_date
			@credit_line = credit_line
			@first_due_date = first_due_date
			@last_interest_date = open_date
			@last_billing_date = open_date
			@had_draw = false
		end
		
		# fees
		def monthly_fee
			MONTHLY_FEE
		end
		
		def draw_fee(amount) 
			[DRAW_FEE_MIN, amount * DRAW_FEE_PCT].max
		end
		

		# ledger amounts
		def principal(date)
			@ledger.balance(LedgerEntry::PRINCIPAL, date)
		end
	
		def fees(date)
			@ledger.balance(LedgerEntry::FEE, date)
		end
	
		def accrued_interest(date)
			move_forward_to(date)
			@ledger.balance(LedgerEntry::INTEREST, date)
		end
	
		def total_balance(date)
			move_forward_to(date)
			@ledger.balance(LedgerEntry::TOTAL, date)
		end
	
		
		# draws
		def draw(date, amount) 
			if (!@had_draw && amount > 0) 
				@ledger.add(LedgerEntry::FEE, "Monthly fee", @last_billing_date, monthly_fee, LedgerEntry::CREDIT)
				@had_draw = true
			end
			move_forward_to(date)
			@ledger.add(LedgerEntry::PRINCIPAL, "Transfer", date, amount, LedgerEntry::CREDIT)
			@ledger.add(LedgerEntry::FEE, "Tranfer fee", date, draw_fee(amount), LedgerEntry::CREDIT)
		end

	
		# interest accrual
		def daily_interest(date) 
			return INTEREST_RATE/365*principal(date)
		end	
		
		def accrue_interest(date) 
			while (@last_interest_date < date) 
				i = daily_interest(@last_interest_date)
				@last_interest_date += 1
				@ledger.add(LedgerEntry::INTEREST, "Accrual  ", @last_interest_date, i, LedgerEntry::CREDIT)
			end
		end

	
		# payments 
		def pay(date, amount)
			move_forward_to(date)
			@payments[date] = amount
			f = [fees(date), amount].min
			@ledger.add(LedgerEntry::FEE, "Payment  ", date, f, LedgerEntry::DEBIT)
			amount -= f
			i = [accrued_interest(date), amount].min
			@ledger.add(LedgerEntry::INTEREST, "Payment  ", date, i, LedgerEntry::DEBIT)
			amount -= i
			@ledger.add(LedgerEntry::PRINCIPAL, "Payment  ", date, amount, LedgerEntry::DEBIT)
		end	

		
		# billing cycles 
		def skip_weekend(date) 
			(date.wday == 6) ? date + 2 : (date.wday == 0) ? date + 1 : date
		end
		
		def next_due_date(date = @last_billing_date) 
			d = @first_due_date
			d = d >> 1 while skip_weekend(d) <= date 
			return skip_weekend(d)
		end

		def prev_due_date(date = @last_billing_date) 
			d = @first_due_date
			d = d >> 1 while skip_weekend(d) <= date 
#			return skip_weekend(d << 1)
			return (@first_due_date <= (d << 1)) ? skip_weekend(d << 1) : nil
		end
	
		
		def next_statement_date(date = @last_billing_date) 
			d = next_due_date(date) - DUE_AFTER
			d = next_due_date(next_due_date(date)) - DUE_AFTER if (d <= date)
			return d
		end
		
		def prev_statement_date(date = @last_billing_date) 
			d = next_due_date(date) - DUE_AFTER
			if (d > date) then
				d = prev_due_date(date).nil? ? nil : prev_due_date(date) - DUE_AFTER
			end
			return d
		end
	
		def generate_statements(date) 
			while next_statement_date(@last_billing_date) <= date 
				@last_billing_date = next_statement_date(@last_billing_date)
				ledger.add(LedgerEntry::FEE, "Monthly fee", @last_billing_date + 1, monthly_fee, LedgerEntry::CREDIT) if (@had_draw)
			end
		end

		def move_forward_to(date) 
			accrue_interest(date)
			generate_statements(date)
		end
		
		# minimum payment
		def denominator
			case 
				when @credit_line <= 350 then 5
				when @credit_line <= 500 then 6
				when @credit_line <= 750 then 8
				else 10
			end
		end
		
		def min_payment(date = @last_billing_date)
			@ledger.outstanding_last_draw(date||@last_billing_date) / denominator
		end

		
		def paid(start_date, end_date) 
			p = 0
			@payments.each do |day, amount|
				p += amount if day >= start_date && day <= end_date
			end
			return p
		end
		
		def payment_due(date = @last_interest_date) 
			p = [0, min_payment(prev_statement_date(date)) - paid(prev_statement_date(date), date)].max
			return [p, total_balance(date)].min
		end
	
		
		# display
		def pad(s, i)
			if COL_JUST[i] == 1 then
				s.to_s[0..COL_WIDTHS[i]-1].ljust(COL_WIDTHS[i])
			else
				s.to_s[0..COL_WIDTHS[i]-1].rjust(COL_WIDTHS[i])
			end
		end

		def to_s(date = @last_interest_date)
			[date,
			 @id,
			 @app_number,
			 @email,
			 @open_date,
			 '%.2f' % @credit_line,
			 '%.2f' % principal(date),
			 '%.2f' % fees(date),
			 '%.2f' % accrued_interest(date),
			 '%.2f' % total_balance(date),
			 prev_due_date(date),
			 next_due_date(date),
			 '%.2f' % min_payment(prev_statement_date(date)),
			 '%.2f' % payment_due(date),
			 @payments.keys.last,
			 @payments.values.last.nil? ? "" : '%.2f' % @payments.values.last
			].each_with_index.map { |s, i|
				(COL_JUST[i] == 1) ? s.to_s[0..COL_WIDTHS[i]-1].ljust(COL_WIDTHS[i]) : s.to_s[0..COL_WIDTHS[i]-1].rjust(COL_WIDTHS[i])
			}.join(" ")
		end

		def to_csv(date = @last_interest_date)
			[date,
			 @id,
			 @app_number,
			 @email,
			 @open_date,
			 '%.2f' % @credit_line,
			 '%.2f' % principal(date),
			 '%.2f' % fees(date),
			 '%.2f' % accrued_interest(date),
			 '%.2f' % total_balance(date),
			 prev_due_date(date),
			 next_due_date(date),
			 '%.2f' % min_payment(prev_statement_date(date)),
			 '%.2f' % payment_due(date),
			 @payments.keys.last,
			 @payments.values.last.nil? ? "" : '%.2f' % @payments.values.last
			].join(",")
		end


	end

end
