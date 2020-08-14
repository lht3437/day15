package com.xiaoshu.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.CompanyMapper;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Company;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.PersonVo;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.UserExample.Criteria;

@Service
public class PersonService {

	@Autowired
	PersonMapper personMapper;


	@Autowired
	CompanyMapper companyMapper;


	public List<Company> findAll() {
		// TODO Auto-generated method stub
		return companyMapper.selectAll();
	}


	public PageInfo<PersonVo> findList(PersonVo personVo, Integer pageNum, Integer pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		List<PersonVo> list=personMapper.findList(personVo);
		return new PageInfo<>(list);
	}


	public void updateUser(Person person) {
		// TODO Auto-generated method stub
		personMapper.updateByPrimaryKeySelective(person);
	}


	public void addUser(Person person) {
		// TODO Auto-generated method stub
		person.setCreateTime(new Date());
		personMapper.insert(person);
	}
	

	
/*	// 通过用户名判断是否存在，（新增时不能重名）
	public User existUserWithUserName(String userName) throws Exception {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(userName);
		List<User> userList = userMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};
*/
	public Person existUserWithUserName(String expressName) throws Exception {
	
		List<Person> userList = personMapper.findByName(expressName);
		return userList.isEmpty()?null:userList.get(0);
	}



	public void deletePerson(Integer id) {
		// TODO Auto-generated method stub
		personMapper.deleteByPrimaryKey(id);
	}


	public List<PersonVo> findLog(PersonVo personVo) {
		// TODO Auto-generated method stub
		return personMapper.findList(personVo);
	}


	public void importPerson(MultipartFile personFile) throws InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		Workbook workbook = WorkbookFactory.create(personFile.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
			int lastRowNum = sheet.getLastRowNum();
			for (int i = 0; i < lastRowNum; i++) {
				Row row = sheet.getRow(i+1);
				String expressName = row.getCell(0).toString();
				String sex = row.getCell(1).toString();
				String expressTrait = row.getCell(2).toString();
				Date entryTime= row.getCell(3).getDateCellValue();
				String cname = row.getCell(4).toString();
				if(!cname.equals("京东")){
					continue;
					
				}
				
				Person p=new Person();
				p.setExpressName(expressName);
				p.setSex(sex);
				p.setExpressTrait(expressTrait);
				p.setEntryTime(entryTime);
				
				/*Company param=new Company();
				param.setExpressName(cname);
				Company company = companyMapper.selectOne(param);
				*/
				int cid =findCompany(cname);
				
				p.setExpressTypeId(cid);
				p.setCreateTime(new Date());
				
				personMapper.insert(p);
			}
			
		
		
	}


	public int findCompany(String cname) {
		// TODO Auto-generated method stub
		Company param=new Company();
		param.setExpressName(cname);
		Company company = companyMapper.selectOne(param);
		if(company==null){
			param.setCreateTime(new Date());
			param.setStatus("快");
			companyMapper.insertCompany(param);
			company=param;
			
		}
		
		
		return company.getId();
	}

	public List<PersonVo> countPerson(){
		return personMapper.countPerson();
		
		
	}

}
