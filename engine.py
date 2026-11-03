import pandas as pd
import numpy as np
from statsmodels.tsa.statespace.sarimax import SARIMAX
import warnings

warnings.filterwarnings("ignore")


class InventoryPredictor:
    def __init__(self):
        pass

    @staticmethod
    def calculate_units_to_buy(df_raw, product_code, lead_time_weeks=4, target_service_level=1.65):
        df = df_raw.copy()

        # 1. Clean dates and format structural string identifiers
        df['orderDate'] = pd.to_datetime(df['orderDate'])
        target_code = str(product_code).strip()

        # Cast columns cleanly to string data types to eliminate NaN dropouts
        df['productId'] = df['productId'].fillna(0).astype(int).astype(str).str.strip()
        df['productCode'] = df['productCode'].astype(str).str.strip()
        df['productMake'] = df['productMake'].astype(str).str.strip()

        # MATCH ALL POSSIBLE FIELDS: Checks productId, productCode, and productMake paths
        product_df = df[
            (df['productId'] == target_code) |
            (df['productCode'] == target_code) |
            (df['productMake'] == target_code)
            ]

        if product_df.empty:
            return {"error": f"Product identifier '{target_code}' could not be matched anywhere in the CSV rows."}

        # 2. Resample scattered records into continuous month segments
        monthly_series = product_df.set_index('orderDate').resample('ME')['quantity'].sum().fillna(0)

        # If the product exists but has zero sales across history, catch it early
        if monthly_series.sum() == 0:
            return {
                "productCode": str(product_code),
                "historicalMonthsTracked": int(len(monthly_series)),
                "expectedNextMonthDemand": 0.0,
                "calculatedSafetyStock": 5.0,
                "leadTimeWeeks": int(lead_time_weeks),
                "suggestedUnitsToBuy": 5  # Recommended order baseline to maintain stock
            }

        # 3. Apply the time-series forecasting engine
        if len(monthly_series) < 3:
            predicted_next_month = monthly_series.mean()
            std_demand = monthly_series.std() if len(monthly_series) > 1 else 5.0
        else:
            try:
                model = SARIMAX(monthly_series, order=(1, 1, 1), enforce_stationarity=False,
                                enforce_invertibility=False)
                model_fit = model.fit(disp=False)
                forecast = model_fit.forecast(steps=1)
                predicted_next_month = max(0.0, float(forecast.iloc[0]))
                std_demand = monthly_series.std()
            except Exception:
                # Rolling fallback for optimization math stability
                predicted_next_month = monthly_series.tail(3).mean()
                std_demand = monthly_series.std()

        # 4. Industrial Replenishment Logic
        lead_time_fraction = lead_time_weeks / 4.0
        lead_time_demand = predicted_next_month * lead_time_fraction
        safety_stock = target_service_level * std_demand * np.sqrt(lead_time_fraction)

        if np.isnan(safety_stock) or safety_stock == 0:
            safety_stock = 5.0

        suggested_to_buy = int(np.ceil(lead_time_demand + safety_stock))

        return {
            "productCode": str(product_code),
            "historicalMonthsTracked": int(len(monthly_series)),
            "expectedNextMonthDemand": round(float(predicted_next_month), 2),
            "calculatedSafetyStock": round(float(safety_stock), 2),
            "leadTimeWeeks": int(lead_time_weeks),
            "suggestedUnitsToBuy": int(suggested_to_buy)
        }
