/*
 * MIT License
 * 
 * Copyright (c) 2018 i4one Interactive, LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.i4one.promotion.model.code;

import com.i4one.base.dao.RecordTypeSqlParameterSource;
import com.i4one.base.dao.terminable.BaseTerminableSiteGroupRecordRowMapper;
import com.i4one.base.model.i18n.IString;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Hamid Badiozamani
 */
public class CodeRecordMapper extends BaseTerminableSiteGroupRecordRowMapper<CodeRecord> implements RowMapper<CodeRecord>
{
	@Override
	protected CodeRecord mapRowInternal(ResultSet res) throws SQLException
	{
		CodeRecord codeRecord = new CodeRecord();

		codeRecord.setOutro(new IString(res.getString("outro")));
		codeRecord.setTitle(new IString(res.getString("title")));
		codeRecord.setCode(res.getString("code"));

		return codeRecord;
	}

	@Override
	protected RecordTypeSqlParameterSource getSqlParameterSourceInternal(CodeRecord o)
	{
		RecordTypeSqlParameterSource sqlParams = new RecordTypeSqlParameterSource();

		sqlParams.addValue("title", o.getTitle());
		sqlParams.addValue("outro", o.getOutro());
		sqlParams.addValue("code", o.getCode());

		return sqlParams;
	}

	@Override
	public CodeRecord emptyInstance()
	{
		return new CodeRecord();
	}
}
