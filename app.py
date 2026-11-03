from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import pandas as pd
import os
from engine import InventoryPredictor

# Initialize the FastAPI web server instance layer
app = FastAPI(
    title="Vojas Inventory Forecasting Engine",
    description="Direct CSV file-stream analytics microservice layer bypassing SQL connections.",
    version="1.0.0"
)

# Relative target filename matching your desktop spreadsheet directory layout
CSV_FILE_PATH = "sales_records.csv"


class QueryPayload(BaseModel):
    product_code: str
    lead_time_weeks: int = 4
    service_level_coefficient: float = 1.65


@app.get("/api/v1/analytics/status")
def get_service_status():
    """Simple health-check endpoint to verify python runtime connectivity."""
    file_exists = os.path.exists(CSV_FILE_PATH)
    return {
        "status": "ONLINE",
        "targetFileIsDetected": file_exists,
        "activeDatasetSource": os.path.abspath(CSV_FILE_PATH) if file_exists else "FILE_NOT_FOUND"
    }


@app.post("/api/v1/analytics/procure-units")
def predict_replenishment_units(payload: QueryPayload):
    """
    HTTP POST Endpoint invoked by Java Spring Boot's RestTemplate client.
    Loads data directly from the CSV spreadsheet file to run SARIMAX trends math.
    """
    # 1. Live system verify that the target spreadsheet file exists locally
    if not os.path.exists(CSV_FILE_PATH):
        raise HTTPException(
            status_code=500,
            detail=f"Required sales data array file missing. Place '{CSV_FILE_PATH}' in this directory: {os.getcwd()}"
        )

    # 2. Parse raw transactional row structures into an in-memory Pandas dataframe matrix
    try:
        df_sales = pd.read_csv(CSV_FILE_PATH)
    except Exception as error:
        raise HTTPException(
            status_code=500,
            detail=f"Error parsing historical CSV row lines structure: {str(error)}"
        )

    # 3. Pass data fields into your proper engine time-series calculation pipeline
    result = InventoryPredictor.calculate_units_to_buy(
        df_raw=df_sales,
        product_code=payload.product_code,
        lead_time_weeks=payload.lead_time_weeks,
        target_service_level=payload.service_level_coefficient
    )

    # 4. Catch column errors or absent row identifiers elegantly
    if "error" in result:
        raise HTTPException(status_code=400, detail=result["error"])

    return result


if __name__ == "__main__":
    import uvicorn

    # Allows starting the script natively by double-clicking or calling python app.py
    uvicorn.run("app:app", host="127.0.0.1", port=8000, reload=True)
