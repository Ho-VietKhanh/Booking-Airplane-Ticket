import React from "react";

const BookFood = ({
  mockFoods,
  foodCounts,
  handleFoodChange,
}) => (
  <div className="bg-white rounded-lg p-4 shadow">
    <div className="flex justify-between items-center mb-2">
      <span className="font-semibold text-purple-700">Concession</span>
      <button className="border border-purple-400 rounded px-3 py-1 text-purple-700 font-semibold text-sm bg-purple-50 cursor-default">
        Concession
      </button>
    </div>
    <div>
      {mockFoods.map((food) => (
        <div key={food.id} className="flex items-center border-b py-2 last:border-b-0">
          <img src={food.img} alt={food.name} className="w-12 h-12 object-cover rounded mr-3" />
          <div className="flex-1">
            <div className="font-semibold text-gray-700 text-sm">{food.name}</div>
            <div className="flex items-center gap-2 mt-1">
              <span className="text-gray-400 line-through text-xs">{food.oldPrice.toLocaleString()} VND</span>
              <span className="text-purple-700 font-bold text-sm">{food.price.toLocaleString()} VND</span>
            </div>
          </div>
          <div className="flex items-center gap-2 ml-2">
            <button
              className="w-6 h-6 rounded-full bg-gray-100 border flex items-center justify-center text-purple-700 font-bold text-lg"
              onClick={() => handleFoodChange(food.id, -1)}
              disabled={foodCounts[food.id] === 0}
            >-</button>
            <span className="w-6 text-center">{foodCounts[food.id]}</span>
            <button
              className="w-6 h-6 rounded-full bg-purple-100 border flex items-center justify-center text-purple-700 font-bold text-lg"
              onClick={() => handleFoodChange(food.id, 1)}
            >+</button>
          </div>
        </div>
      ))}
    </div>
  </div>
);

export default BookFood;