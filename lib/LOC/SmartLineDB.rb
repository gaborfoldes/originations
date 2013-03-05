
require "mysql"
require "yaml"

module LOC

	class SmartLineDB

		L = """
			select 
				date(loc.created_at) as line_opened,
				loc.id as id,
				la.application_number,
				u.id as user_id,
				em.email_address,
				loc.credit_line,
				loc.first_payment_date
			from
				emails em
				join users u on u.id = em.user_id
				join loan_applications la on la.user_id = u.id
				join line_of_credits loc on loc.loan_application_id = la.id
			"""
	
		D = """
			select
				op.line_of_credit_id as id,
				date(op.created_at) draw_date,
				op.amount
			from
				outgoing_payments op
			where
				op.state in ('bill_paid', 'sent_to_recipient', 'new')
				and op.type = 'LineOfCreditOutgoingPayment'
			"""
		
		P = """
			select
				lp.line_of_credit_id as id,
				date(lp.initiated_on) payment_date,
				lp.amount
			from
				loan_payments lp
			where
				lp.state in ('in_progress', 'completed')
				and lp.type = 'LineOfCreditPayment'
			"""

		def self.open
			if !defined? @@db
				params = YAML.load_file('../database.yml')
				@@db = Mysql.new(*params.values)
			end
		end

		def self.get_line(id=nil)
			self.open
			return @@db.query L + (id.nil? ? "" : "where loc.id = " + id.to_s)
		end

		def self.get_draws(id=nil)
			return @@db.query D + (id.nil? ? "" : "and op.line_of_credit_id = " + id.to_s)
		end

		def self.get_payments(id=nil)
			return @@db.query P + (id.nil? ? "" : "and lp.line_of_credit_id = " + id.to_s)
		end

		def self.ids_by_email(email)
			self.open
			ids = []
			q = @@db.query L + "where em.email_address like '%" + email.to_s + "%'"
			q.each do |row|
				ids.push row[1].to_i
			end
			return ids
		end

		def self.ids_by_app_number(app_number)
			ids = []
			q = @@db.query L + "where la.application_number like '%" + app_number.to_s + "%'"
			q.each do |row|
				ids.push row[1].to_i
			end
			return ids
		end

		def self.close
			@@db.close
		end

	end

end
