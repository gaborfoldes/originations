
require "date"

module LOC

	class Lines

		SEPARATOR = "\t"

		attr_accessor :lines	
	
		def initialize
			@lines = {}
			@exclusions = {}
		end
		
		def put(line) 
			@lines[line.id] = line
		end


		# display info
		def print(ids=nil)
			puts LineOfCredit::HEADER + "\n"
			if ids.nil? then
				@lines.each do |id, line| 
					puts line.to_s + "\n"
				end
			else
				ids = [ids] if ids.is_a? Fixnum
				ids.each do |id|
					puts lines[id].to_s + "\n"
				end
			end
			return "Done"
		end

		def print_csv(ids=nil)
			puts LineOfCredit::COL_NAMES.join(",") + "\n"
			if ids.nil? then
				@lines.each do |id, line| 
					puts line.to_csv + "\n"
				end
			else
				ids = [ids] if ids.is_a? Fixnum
				ids.each do |id|
					puts lines[id].to_csv + "\n"
				end
			end
			return "Done"
		end

		
		def show(ids)
			build(ids)
			print(ids)
#			puts lines[id].ledger
		end
		
		# load data

		def by_email(email)
			return SmartLineDB.ids_by_email(email)
		end

		def by_app_number(app_number)
			return SmartLineDB.ids_by_app_number(app_number)
		end

		def build(ids=nil)
			
			if ids.nil? then
				SmartLineDB.get_line.each { |row| parse_line row }
				SmartLineDB.get_draws.each { |row| parse_draw row }
				SmartLineDB.get_payments.each { |row| parse_payment row }
				move_forward_to Date.today
			else
				ids = [ids] if ids.is_a? Fixnum
				ids.each do |id|
					SmartLineDB.get_line(id).each { |row| parse_line row }
					SmartLineDB.get_draws(id).each { |row| parse_draw row }
					SmartLineDB.get_payments(id).each { |row| parse_payment row }
					@lines[id].move_forward_to Date.today
				end
			end
			return "Done"
		end 

		def parse_line(s)
			s[0] = Date.parse(s[0])
			s[1] = s[1].to_i
			s[3] = s[3].to_i
			s[5] = s[5].to_f
			s[6] = Date.parse(s[6])
			put(LineOfCredit.new(*s))
		end

		def load_lines(file_name) 
			File.open(file_name, "r").each_line do |row|
				parse_line row.strip.split(SEPARATOR)
			end
		end

		def parse_draw(s)
			l = @lines[s[0].to_i]
			l.draw(Date.parse(s[1]), s[2].to_f) if !l.nil?
		end

		def load_draws(file_name) 
			File.open(file_name, "r").each_line do |row|
				parse_draw row.strip.split(SEPARATOR)
			end
		end
			
		def parse_payment(s)
			l = @lines[s[0].to_i]
			l.pay(Date.parse(s[1]), s[2].to_f) if !l.nil?
		end

		def load_payments(file_name) 
			File.open(file_name, "r").each_line do |row|
				parse_payment row.strip.split(SEPARATOR)
			end
		end			


		
		# get all lines in sync
		def move_forward_to(date) 
			@lines.each do |id, line|
				line.move_forward_to date 
			end
		end

		# queries
		def lines_due(date) 
			ids = []
			@lines.each do |id, line|
				ids.push id if line.next_due_date == date
			end
			return ids
		end
		
	end		
		
end
	