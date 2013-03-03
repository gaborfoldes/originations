
require "date"

module LOC

	class Ledger

		attr_accessor :entries

		def initialize
			@entries = []
		end
		
		def add(*params)
			@entries.push LedgerEntry.new(*params)
		end

		def balance(type, date=Date.today)
			s = 0
			@entries.each do |entry|
				if entry.post_date <= date && (type == entry.type || type == LedgerEntry::TOTAL)
					s += entry.signed_amount
				end
			end
			return s
		end
		
		def outstanding_last_draw(date=Date.today)
			@entries.sort! { |a,b| a.post_date <=> b.post_date }
			last_draw = 0
			s = 0
			@entries.each do |entry|
				if entry.post_date <= date && entry.type == LedgerEntry::PRINCIPAL
					s += entry.signed_amount
					last_draw = s if (entry.credit == 1)
				end
			end
			return last_draw
		end
		
		def to_s
			@entries.sort! { |a,b| a.post_date <=> b.post_date }
			all = LedgerEntry::HEADER + "\n"
			@entries.each do |entry|
				all += entry.to_s + "\n"
			end
			return all
		end
	
	end

end