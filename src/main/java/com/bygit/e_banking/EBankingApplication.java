package com.bygit.e_banking;

import com.bygit.e_banking.dtos.BankAccountDTO;
import com.bygit.e_banking.dtos.CurrentBankAccountDTO;
import com.bygit.e_banking.dtos.CustomerDTO;
import com.bygit.e_banking.dtos.SavingBankAccountDTO;
import com.bygit.e_banking.entities.AccountOperation;
import com.bygit.e_banking.entities.CurrentAccount;
import com.bygit.e_banking.entities.Customer;
import com.bygit.e_banking.entities.SavingAccount;
import com.bygit.e_banking.enums.AccountStatus;
import com.bygit.e_banking.enums.OperationType;
import com.bygit.e_banking.exceptions.CustomerNotFoundException;
import com.bygit.e_banking.repositories.AccountOperationRepository;
import com.bygit.e_banking.repositories.BankAccountRepository;
import com.bygit.e_banking.repositories.CustomerRepository;
import com.bygit.e_banking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EBankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(EBankingApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
		//Creer et inserer des cli
		return args -> {
			Stream.of("Hassan","Imane","Mohamed").forEach(name->{
				CustomerDTO customer=new CustomerDTO();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customer);
			});
			//pour ces clie je crée je crée deux comptes
			bankAccountService.listCustomers().forEach(customer->{
				try {
					bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());

				} catch (CustomerNotFoundException e) {
					e.printStackTrace();
				}
			});
			List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
			for (BankAccountDTO bankAccount:bankAccounts){
				for (int i = 0; i <10 ; i++) {
					String accountId;
					if(bankAccount instanceof SavingBankAccountDTO){
						accountId=((SavingBankAccountDTO) bankAccount).getId();
					} else{
						accountId=((CurrentBankAccountDTO) bankAccount).getId();
					}
					bankAccountService.credit(accountId,10000+Math.random()*120000,"Credit");
					bankAccountService.debit(accountId,1000+Math.random()*9000,"Debit");
				}
			}
		};
	}
	//@Bean
	CommandLineRunner start(CustomerRepository customerRepository,
							BankAccountRepository bankAccountRepository,
							AccountOperationRepository accountOperationRepository){
		return args -> {
			Stream.of("Hassan","Yassine","Aicha").forEach(name->{
				Customer customer=new Customer();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				customerRepository.save(customer);
			});
			customerRepository.findAll().forEach(cust->{
				CurrentAccount currentAccount=new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random()*90000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverDraft(9000);
				bankAccountRepository.save(currentAccount);

				SavingAccount savingAccount=new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random()*90000);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(cust);
				savingAccount.setInterestRate(5.5);
				bankAccountRepository.save(savingAccount);

			});
			bankAccountRepository.findAll().forEach(acc->{
				for (int i = 0; i <10 ; i++) {
					AccountOperation accountOperation=new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random()*12000);
					accountOperation.setType(Math.random()>0.5? OperationType.DEBIT: OperationType.CREDIT);
					accountOperation.setBankAccount(acc);
					accountOperationRepository.save(accountOperation);
				}

			});
		};

	}

}
