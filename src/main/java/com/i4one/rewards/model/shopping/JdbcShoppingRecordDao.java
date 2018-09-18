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
package com.i4one.rewards.model.shopping;

import com.i4one.base.model.report.ReportDriver;
import com.i4one.base.model.report.TopLevelReport;
import com.i4one.rewards.dao.BaseJdbcRewardsRecordDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * @author Hamid Badiozamani
 */
@Repository
public class JdbcShoppingRecordDao extends BaseJdbcRewardsRecordDao<ShoppingRecord> implements ShoppingRecordDao
{
	@Override
	protected ShoppingRecordMapper initMapper()
	{
		return new ShoppingRecordMapper();
	}

	/*
	 * If we wanted to disable switching prizes altogether. Currently, the
	 * manager allows it if the shopping item has not been purchased
	 *
	@Override
	public void updateBySer(ShoppingRecord o, String... columnList)
	{
		String[] updateColumnList = new String[columnList.length];

		ArrayList<String> columns = new ArrayList<>(columnList.length);
		for ( String column : columnList )
		{
			// Filter out the prizeid since we don't want it changed once
			// the record is created
			//
			if ( column != null && !column.equalsIgnoreCase("prizeid"))
			{
				columns.add(column);
			}
		}

		updateColumnList = columns.toArray(updateColumnList);
		super.updateBySer(o, updateColumnList);
	}
	*/
	
	@Override
	public void processReport(ShoppingRecord trivia, TopLevelReport report)
	{
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("itemid", trivia.getSer());

		report.initReport();
		getLogger().debug("Pre-processed report: " + report);

		ShoppingPurchaseRecord shoppingPurchases = new ShoppingPurchaseRecord();
		getNamedParameterJdbcTemplate().query("SELECT * FROM " + shoppingPurchases.getFullTableName() + " WHERE itemid = :itemid",
			sqlParams, new ReportDriver(report, new ShoppingPurchaseRecordMapper()));
	}
}
