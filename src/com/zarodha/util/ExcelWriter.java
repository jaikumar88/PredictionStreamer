package com.zarodha.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.zarodha.model.OutputFeed;

public class ExcelWriter {

	private String OUT_FILE = "D:/generated_Feed.xlsx";
	private static String[] columns = { "Exch.","Descr.", "Time","Bid Qty","Bid","Ask","Ask Qty","LTP","Net Chg","%Chg", "Volume","OI","Open","High","Low","Close","Last Traded Time","PQU","Expiry" };
	static {
		Calendar cal = Calendar.getInstance();
	}

	public void writeOutput(List<OutputFeed> outputFeet) {
		try {
			File file = new File(OUT_FILE);
			if (file.exists()) {
				updateSheet(outputFeet);
			} else {
				createSheet(outputFeet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createSheet(List<OutputFeed> outputFeet) {
		try {
			FileOutputStream fileOut;
			fileOut = new FileOutputStream(OUT_FILE, false);
			// Create a Workbook
			Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for
													// generating `.xls` file
			CreationHelper createHelper = workbook.getCreationHelper();
			// Create a Sheet
			Sheet sheet = null;
			sheet = workbook.createSheet("Output");

			// Create a Font for styling header cells
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 14);
			headerFont.setColor(IndexedColors.RED.getIndex());

			// Create a CellStyle with the font
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
				// Create a Row
				Row headerRow = sheet.createRow(0);

				// Create cells
				for (int i = 0; i < columns.length; i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columns[i]);
					cell.setCellStyle(headerCellStyle);
				}
			

			// Create Cell Style for formatting Date
			CellStyle dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

			// Create Other rows and cells with employees data
			int rowNum = 1;

			for (OutputFeed out : outputFeet) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(out.getInstrument().split(":")[0]);
				row.createCell(1).setCellValue(out.getInstrument());
				row.createCell(2).setCellValue(out.getTime());
				row.createCell(7).setCellValue(out.getLtp());
			}

			// Resize all columns to fit the content size
			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}

			// Write the output to a file

			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void updateSheet(List<OutputFeed> outputFeet) {

		try {
			FileInputStream inputStream = new FileInputStream(new File(OUT_FILE));
            Workbook workbook = WorkbookFactory.create(inputStream);
			
			Sheet sheet = workbook.getSheetAt(0);

			// Create Other rows and cells with employees data
			int rowNum = sheet.getLastRowNum();

			for (OutputFeed out : outputFeet) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(out.getInstrument().split(":")[0]);
				row.createCell(1).setCellValue(out.getInstrument());
				row.createCell(2).setCellValue(out.getTime());
				row.createCell(7).setCellValue(out.getLtp());
			}

			// Resize all columns to fit the content size
			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}
			FileOutputStream fileOut = new FileOutputStream(new File(OUT_FILE),false);
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
